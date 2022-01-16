package routine

import (
	app "fitness-dimension/internal/app/routine"
	pb "fitness-dimension/pkg/api/proto"

	"github.com/go-pg/pg/v10"
	"github.com/labstack/echo/v4"
	"google.golang.org/grpc"
)

func GrpcRoutineServe(s *grpc.Server, db *pg.DB) {
	c := RoutineApiServer{DB: db}
	pb.RegisterRoutineAPIServer(s, &c)
}

func HttpRoutineServe(e *echo.Group, r app.RoutineRepository, p app.RoutinePublisher) {

	service := app.RoutineService{Repository: r, Publisher: p}
	controller := RoutineHttpController{Service: service}

	e.POST("", controller.Create)
	e.GET("", controller.GetAll)
	e.GET("/:id", controller.Get)
	e.PUT("/:id", controller.Update)
	e.DELETE("/:id", controller.Delete)

	e.POST("/:id/training/:idt", controller.AddTraining)
	e.DELETE("/:id/training/:idt", controller.RemoveTraining)
}
