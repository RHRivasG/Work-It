package grpc

import (
	"net"
	"summary-service/pkg/server"

	"github.com/go-pg/pg/v10"
	"google.golang.org/grpc"
)

func GrpcServe(l net.Listener, database *pg.DB) error {
	maxMsgSize := 220 * 1024 * 1024
	s := grpc.NewServer(
		grpc.MaxMsgSize(maxMsgSize),
		grpc.MaxRecvMsgSize(maxMsgSize),
		grpc.MaxSendMsgSize(maxMsgSize),
	)

	server.GrpcSummaryServe(s, database)

	return s.Serve(l)
}
