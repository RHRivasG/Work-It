package server

import (
	"fitness-dimension/application/trainings"
	pb "fitness-dimension/gen/proto"
	"fitness-dimension/service/app/controllers"

	"github.com/go-pg/pg/v10"
	"github.com/labstack/echo/v4"
	"google.golang.org/grpc"
)

func HttpTrainingServe(e *echo.Echo, r trainings.TrainingRepository, p trainings.TrainingPublisher) {

	service := trainings.TrainingService{Repository: r, Publisher: p}
	controller := controllers.TrainingHttpController{Service: service}

	e.GET("/trainings", controller.GetAll)
	e.GET("/trainings/:id", controller.Get)
	e.POST("/trainings", controller.Create)
	e.PUT("/trainings/:id", controller.Update)
	e.DELETE("/trainings/:id", controller.Delete)

	e.GET("/trainings/:id/video", controller.GetVideo)
	e.POST("/trainings/:id/video", controller.CreateVideo)
	e.DELETE("/trainings/:id/video", controller.DeleteVideo)

	e.GET("/trainings/trainer", controller.GetByTrainer)
}

func GrpcTrainingServe(s *grpc.Server, db *pg.DB) {
	c := controllers.TrainingApiServer{DB: db}
	pb.RegisterTrainingAPIServer(s, &c)
}
