package server

import (
	"fitness-dimension/application/routines"
	"fitness-dimension/service/app/controllers"

	"github.com/go-pg/pg/v10"
	"github.com/labstack/echo/v4"
	"google.golang.org/grpc"

	pb "fitness-dimension/gen/proto"
)

func HttpRoutineServe(e *echo.Echo, r routines.RoutineRepository, p routines.RoutinePublisher) {

	service := routines.RoutineService{Repository: r, Publisher: p}
	controller := controllers.RoutineHttpController{Service: service}

	e.POST("/routines", controller.Create)
	e.GET("/routines", controller.GetAll)
	e.GET("/routines/:id", controller.Get)
	e.PUT("/routines/:id", controller.Update)
	e.DELETE("/routines/:id", controller.Delete)

	e.POST("/routines/:id/training/:idt", controller.AddTraining)
	e.DELETE("/routines/:id/training/:idt", controller.RemoveTraining)
}

func GrpcRoutineServe(s *grpc.Server, db *pg.DB) {
	c := controllers.RoutineApiServer{DB: db}
	pb.RegisterRoutineAPIServer(s, &c)
}
