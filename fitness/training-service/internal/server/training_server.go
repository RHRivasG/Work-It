package server

import (
	pb "training-service/pkg/api/proto"
	"training-service/pkg/application"

	"github.com/go-pg/pg/v10"
	"github.com/labstack/echo/v4"
	"google.golang.org/grpc"
)

func GrpcTrainingServe(s *grpc.Server, db *pg.DB) {
	c := TrainingApiServer{DB: db}
	pb.RegisterTrainingAPIServer(s, &c)
}

func HttpTrainingServe(e *echo.Group, r application.TrainingRepository, p application.TrainingPublisher) {

	service := application.TrainingService{Repository: r, Publisher: p}
	controller := TrainingHttpController{Service: service}

	e.POST("", controller.Create)
	e.GET("", controller.GetAll)
	e.GET("/:id", controller.Get)
	e.PUT("/:id", controller.Update)
	e.DELETE("/:id", controller.Delete)

	e.GET("/:id/video", controller.GetVideo)
	e.GET("/:id/video/metadata", controller.GetVideoMetadata)
	e.POST("/:id/video", controller.CreateVideo)
	e.DELETE("/:id/video", controller.DeleteVideo)

	e.GET("/trainer", controller.GetByTrainer)
}
