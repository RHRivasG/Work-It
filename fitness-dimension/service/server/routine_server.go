package server

import (
	"fitness-dimension/application/routines"
	"fitness-dimension/service/app/controllers"

	"github.com/go-pg/pg/v10"
	"github.com/labstack/echo/v4"
	"google.golang.org/grpc"

	pb "fitness-dimension/gen/proto"
)

func HttpRoutineServe(e *echo.Group, r routines.RoutineRepository, p routines.RoutinePublisher) {

	service := routines.RoutineService{Repository: r, Publisher: p}
	controller := controllers.RoutineHttpController{Service: service}

	e.POST("", controller.Create)
	e.GET("", controller.GetAll)
	e.GET("/:id", controller.Get)
	e.PUT("/:id", controller.Update)
	e.DELETE("/:id", controller.Delete)

	e.POST("/:id/training/:idt", controller.AddTraining)
	e.DELETE("/:id/training/:idt", controller.RemoveTraining)
}

func GrpcRoutineServe(s *grpc.Server, db *pg.DB) {
	c := controllers.RoutineApiServer{DB: db}
	pb.RegisterRoutineAPIServer(s, &c)
}
