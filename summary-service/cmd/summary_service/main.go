package main

import (
	"log"
	"net"
	"summary-service/pkg/db"
	"summary-service/pkg/env"
	"summary-service/pkg/grpc"
	"summary-service/pkg/http"

	"github.com/go-pg/pg/v10"
	"github.com/soheilhy/cmux"
	"golang.org/x/sync/errgroup"
)

func main() {

	//Env
	if err := env.LoadEnv(); err != nil {
		log.Fatal(err)
	}

	//Database
	database, err := db.ConnectDatabase()
	if err != nil {
		log.Fatal(err)
	}
	defer database.Close()

	//Server
	if err := serve(database); err != nil {
		log.Fatal(err)
	}
}

func serve(database *pg.DB) error {
	listener, err := net.Listen("tcp", ":8081")
	if err != nil {
		return err
	}

	m := cmux.New(listener)

	httpListener := m.Match(cmux.HTTP1())
	grpcListener := m.MatchWithWriters(cmux.HTTP2MatchHeaderFieldSendSettings("content-type", "application/grpc"))

	g := new(errgroup.Group)
	g.Go(func() error { return grpc.GrpcServe(grpcListener, database) })
	g.Go(func() error { return http.HtttpServe(httpListener, database) })

	g.Go(func() error { return m.Serve() })
	log.Println("run server on port 8081:")
	g.Wait()

	return nil
}
