package service

import (
	"context"
	"fitness-dimension/service/config"
	"fitness-dimension/service/server"
	"log"
	"net"

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
	defer conn.Close()
	client := pb.NewServiceAggregatorClient(conn)
	res, err := client.AddService(context.Background(), &pb.AddServiceMessage{
		Group:    "fitness",
		Capacity: 1,
	})
	if err != nil {
		log.Fatal(err)
	}
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
