package main

import (
	"context"
	"crypto/rand"
	"crypto/tls"
	"crypto/x509"
	"fitness-dimension/pkg/auth"
	"fitness-dimension/pkg/db"
	"fitness-dimension/pkg/server/aggregator"
	"fitness-dimension/pkg/server/routine"
	"fitness-dimension/pkg/server/training"
	"fmt"
	"io/ioutil"
	"log"
	"net"
	"net/http"
	"os"
	"os/signal"
	"syscall"

	pb "fitness-dimension/pkg/api/proto"

	"github.com/go-pg/pg/v10"
	"github.com/labstack/echo/v4"
	"github.com/labstack/echo/v4/middleware"
	"github.com/soheilhy/cmux"
	"golang.org/x/net/http2"
	"golang.org/x/sync/errgroup"
	"google.golang.org/grpc"
	"google.golang.org/grpc/credentials"
)

func main() {

	//Service Aggregator
	conn, client, err := aggregator.SetServiceAggregator()
	if err != nil {
		log.Fatal(err)
	}
	defer conn.Close()
	defer client.Unsubscribe(context.Background(), &pb.UnsubscribeMessage{})

	//Database
	database, err := db.ConnectDatabase()
	if err != nil {
		log.Fatal(err)
	}
	defer database.Close()

	sigChannel := make(chan os.Signal, 1)
	signal.Notify(sigChannel, os.Interrupt, syscall.SIGTERM)
	go aggregator.CleanUp(client, conn, sigChannel)
	go db.CleanUp(database, sigChannel)

	//Server
	if err := serve(database); err != nil {
		log.Fatal(err)
	}
}

func serve(db *pg.DB) error {
	listener, err := net.Listen("tcp", ":8080")
	if err != nil {
		return err
	}

	m := cmux.New(listener)

	httpListener := m.Match(cmux.HTTP1())
	otherwiseL := m.Match(cmux.Any())

	tlsl := gimmeTLS(otherwiseL, "../certs/fitness/cert.pem", "../certs/fitness/key.pem")
	tlsm := cmux.New(tlsl)

	grpcListener := tlsm.MatchWithWriters(cmux.HTTP2MatchHeaderFieldSendSettings("content-type", "application/grpc"))

	g := new(errgroup.Group)
	g.Go(func() error { return grpcServe(grpcListener, db) })
	g.Go(func() error { return httpServe(httpListener, db) })

	g.Go(func() error { return m.Serve() })
	g.Go(func() error { return tlsm.Serve() })
	log.Println("run server on port 8080:")
	g.Wait()

	return nil
}

func gimmeTLS(tcpl net.Listener, cert, key string) (tlsl net.Listener) {
	certificate, err := tls.LoadX509KeyPair(cert, key)
	if err != nil {
		log.Fatal(err)
	}

	const requiredCipher = tls.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256
	config := &tls.Config{
		CipherSuites: []uint16{requiredCipher},
		NextProtos:   []string{http2.NextProtoTLS, "h2-14"}, // h2-14 is just for compatibility. will be eventually removed.
		Certificates: []tls.Certificate{certificate},
	}
	config.Rand = rand.Reader

	tlsl = tls.NewListener(tcpl, config)
	return
}

func grpcServe(l net.Listener, db *pg.DB) error {

	//TLS
	// tlsCredentials, err := loadTLSCredentials()
	// if err != nil {
	// 	return err
	// }

	maxMsgSize := 220 * 1024 * 1024
	s := grpc.NewServer(
		grpc.MaxMsgSize(maxMsgSize),
		grpc.MaxRecvMsgSize(maxMsgSize),
		grpc.MaxSendMsgSize(maxMsgSize),
	)

	routine.GrpcRoutineServe(s, db)
	training.GrpcTrainingServe(s, db)

	return s.Serve(l)
}

func httpServe(l net.Listener, db *pg.DB) error {

	tlsCredentials, err := loadTLSCredentials()
	if err != nil {
		return err
	}

	maxMsgSize := 220 * 1024 * 1024
	conn, err := grpc.Dial(
		"localhost:8080",
		grpc.WithTransportCredentials(tlsCredentials),
		grpc.WithDefaultCallOptions(
			grpc.MaxCallRecvMsgSize(maxMsgSize),
			grpc.MaxCallSendMsgSize(maxMsgSize),
		),
	)
	if err != nil {
		return err
	}
	defer conn.Close()

	//Http Server
	e := echo.New()

	//CORS
	e.Use(middleware.CORSWithConfig(middleware.CORSConfig{
		AllowOrigins:     []string{"*"},
		AllowMethods:     []string{http.MethodGet, http.MethodHead, http.MethodPut, http.MethodPatch, http.MethodPost, http.MethodDelete, http.MethodOptions},
		AllowCredentials: true,
	}))

	//Groups
	t := e.Group("/trainings")
	r := e.Group("/routines")

	//Auth
	t.Use(auth.AuthMiddleware())
	r.Use(auth.AuthMiddleware())

	//Clients
	routineClient := pb.NewRoutineAPIClient(conn)
	trainingClient := pb.NewTrainingAPIClient(conn)

	//Repositories
	routineRepository := routine.PgRoutineRepository{DB: db}
	trainingRepository := training.PgTrainingRepository{DB: db}

	//Publishers
	routinePublisher := routine.RoutinePublisher{Client: routineClient}
	trainingPublisher := training.TrainingPublisher{Client: trainingClient}

	//Routes
	routine.HttpRoutineServe(r, routineRepository, &routinePublisher)
	training.HttpTrainingServe(t, trainingRepository, &trainingPublisher)

	s := &http.Server{Handler: e}
	return s.Serve(l)
}

func loadTLSCredentials() (credentials.TransportCredentials, error) {
	// Load certificate of the CA who signed server's certificate
	pemServerCA, err := ioutil.ReadFile("../certs/ca/cert.pem")
	if err != nil {
		return nil, err
	}

	certPool := x509.NewCertPool()
	if !certPool.AppendCertsFromPEM(pemServerCA) {
		return nil, fmt.Errorf("failed to add server CA's certificate")
	}

	// Create the credentials and return it
	config := &tls.Config{
		RootCAs: certPool,
	}

	return credentials.NewTLS(config), nil
}
