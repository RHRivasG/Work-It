package service

import (
	"fitness-dimension/service/config"
	"fitness-dimension/service/server"
	"log"
	"net"

	"github.com/go-pg/pg/v10"
	"github.com/soheilhy/cmux"
	"golang.org/x/sync/errgroup"
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
	db, err := config.ConnectDatabase()
	if err != nil {
		panic(err)
	}

	defer db.Close()
	if err := serve(db); err != nil {
		log.Fatal(err)
	}

}
