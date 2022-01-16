package server

import (
	"fitness-dimension/application/trainings"
	pb "fitness-dimension/gen/proto"
	"fitness-dimension/service/app/controllers"

	"github.com/go-pg/pg/v10"
	"github.com/labstack/echo/v4"
	"google.golang.org/grpc"
)

func HttpTrainingServe(e *echo.Group, r trainings.TrainingRepository, p trainings.TrainingPublisher) {

	service := trainings.TrainingService{Repository: r, Publisher: p}
	controller := controllers.TrainingHttpController{Service: service}

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

func GrpcTrainingServe(s *grpc.Server, db *pg.DB) {
	c := controllers.TrainingApiServer{DB: db}
	pb.RegisterTrainingAPIServer(s, &c)
}
