package server

import (
	"summary-service/internal/application"
	pb "summary-service/pkg/api/proto"

	"github.com/go-pg/pg/v10"
	"github.com/labstack/echo/v4"
	"google.golang.org/grpc"
)

func GrpcSummaryServe(s *grpc.Server, db *pg.DB) {
	h := SummaryHandler{DB: db}
	pb.RegisterSummaryAPIServer(s, &h)
}

func HtttpSummaryServe(e *echo.Echo, database *pg.DB, conn *grpc.ClientConn) {
	client := pb.NewSummaryAPIClient(conn)
	publisher := SummaryPublisher{Client: client}
	repository := PgSummaryRepository{DB: database}
	service := application.SummaryService{
		Publisher:  publisher,
		Repository: repository,
	}

	controller := SummaryController{Service: service}
	e.GET("routines/:id/summary", controller.Get)
	e.POST("routines/:id/summary", controller.Create)
	e.PUT("routines/:id/summary/:ids", controller.Update)
}
