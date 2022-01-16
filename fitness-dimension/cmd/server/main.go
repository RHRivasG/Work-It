package main

import (
	"context"
	"fitness-dimension/pkg/auth"
	"fitness-dimension/pkg/db"
	"fitness-dimension/pkg/server/aggregator"
	"fitness-dimension/pkg/server/routine"
	"fitness-dimension/pkg/server/training"
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
	"golang.org/x/sync/errgroup"
	"google.golang.org/grpc"
)

func main() {

	sigChannel := make(chan os.Signal, 1)
	signal.Notify(sigChannel, os.Interrupt, syscall.SIGTERM)

	//Service Aggregator
	conn, client, err := aggregator.SetServiceAggregator()
	if err != nil {
		panic(err)
	}
	defer client.Unsubscribe(context.Background(), &pb.UnsubscribeMessage{})
	defer conn.Close()
	go aggregator.CleanUp(client, conn, sigChannel)

	//Database
	database, err := db.ConnectDatabase()
	if err != nil {
		panic(err)
	}
	defer database.Close()
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

	grpcListener := m.MatchWithWriters(cmux.HTTP2MatchHeaderFieldSendSettings("content-type", "application/grpc"))
	httpListener := m.Match(cmux.HTTP1())

	g := new(errgroup.Group)
	g.Go(func() error { return grpcServe(grpcListener, db) })
	g.Go(func() error { return httpServe(httpListener, db) })

	g.Go(func() error { return m.Serve() })
	log.Println("run server on port 8080:")
	g.Wait()

	return nil
}

func grpcServe(l net.Listener, db *pg.DB) error {
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

	maxMsgSize := 220 * 1024 * 1024
	conn, err := grpc.Dial(
		"localhost:8080",
		grpc.WithInsecure(),
		grpc.WithDefaultCallOptions(
			grpc.MaxCallRecvMsgSize(maxMsgSize),
			grpc.MaxCallSendMsgSize(maxMsgSize),
		),
	)
	defer conn.Close()
	if err != nil {
		return err
	}

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
