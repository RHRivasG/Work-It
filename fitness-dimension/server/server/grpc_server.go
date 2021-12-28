package server

import (
	"net"

	"github.com/go-pg/pg/v10"
	"google.golang.org/grpc"
)

func GrpcServe(l net.Listener, db *pg.DB) error {
	s := grpc.NewServer()

	GrpcRoutineServe(s, db)
	GrpcTrainingServe(s, db)

	return s.Serve(l)
}
