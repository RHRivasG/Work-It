package server

import (
	"net"

	"github.com/go-pg/pg/v10"
	"google.golang.org/grpc"
)

func GrpcServe(l net.Listener, db *pg.DB) error {
	maxMsgSize := 220 * 1024 * 1024
	s := grpc.NewServer(
		grpc.MaxMsgSize(maxMsgSize),
		grpc.MaxRecvMsgSize(maxMsgSize),
		grpc.MaxSendMsgSize(maxMsgSize),
	)

	GrpcRoutineServe(s, db)
	GrpcTrainingServe(s, db)

	return s.Serve(l)
}
