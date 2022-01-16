package service

import (
	"context"
	"fitness-dimension/service/config"
	"fitness-dimension/service/server"
	"log"
	"net"
	"os"
	"os/signal"
	"syscall"

	"github.com/go-pg/pg/v10"
	"github.com/soheilhy/cmux"
	"golang.org/x/sync/errgroup"
	"google.golang.org/grpc"

	pb "fitness-dimension/gen/proto"
)

func serve(db *pg.DB) error {
	listener, err := net.Listen("tcp", ":8080")
	if err != nil {
		return err
	}

	m := cmux.New(listener)

	grpcListener := m.MatchWithWriters(cmux.HTTP2MatchHeaderFieldSendSettings("content-type", "application/grpc"))
	httpListener := m.Match(cmux.HTTP1())

	g := new(errgroup.Group)
	g.Go(func() error { return server.GrpcServe(grpcListener, db) })
	g.Go(func() error { return server.HttpServe(httpListener, db) })

	g.Go(func() error { return m.Serve() })
	log.Println("run server on port 8080:")
	g.Wait()

	return nil
}

func Main() {

	//Service Aggregator
	host := config.GoDotEnvVariable("SERVICE_AGGREGATOR")
	conn, err := grpc.Dial(host, grpc.WithInsecure())
	if err != nil {
		log.Fatal(err)
	}
	client := pb.NewServiceAggregatorClient(conn)
	res, err := client.AddService(context.Background(), &pb.AddServiceMessage{
		Group:    "fitness",
		Capacity: 1,
	})
	if err != nil {
		log.Fatal(err)
	}

	sigChannel := make(chan os.Signal, 1)
	signal.Notify(sigChannel, os.Interrupt, syscall.SIGTERM)

	go cleanup(conn, client, sigChannel)
	log.Println(res)

	//Database
	db, err := config.ConnectDatabase()
	if err != nil {
		panic(err)
	}

	defer db.Close()
	if err := serve(db); err != nil {
		log.Fatal(err)
	}

}

func cleanup(conn *grpc.ClientConn, client pb.ServiceAggregatorClient, sigChannel chan os.Signal) {
	select {
	case <-sigChannel:
		client.Unsubscribe(context.Background(), &pb.UnsubscribeMessage{})
		conn.Close()
		os.Exit(0)
	}
}
