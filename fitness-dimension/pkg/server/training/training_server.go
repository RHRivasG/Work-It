package training

import (
	app "fitness-dimension/internal/app/training"
	pb "fitness-dimension/pkg/api/proto"

	"github.com/go-pg/pg/v10"
	"github.com/labstack/echo/v4"
	"google.golang.org/grpc"
)

func GrpcTrainingServe(s *grpc.Server, db *pg.DB) {
	c := TrainingApiServer{DB: db}
	pb.RegisterTrainingAPIServer(s, &c)
}

func HttpTrainingServe(e *echo.Group, r app.TrainingRepository, p app.TrainingPublisher) {

	service := app.TrainingService{Repository: r, Publisher: p}
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
