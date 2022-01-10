package server

import (
	pb "fitness-dimension/gen/proto"
	"fitness-dimension/service/app/auth"
	"fitness-dimension/service/app/publishers"
	"fitness-dimension/service/app/repositories"

	"net"
	"net/http"

	"github.com/go-pg/pg/v10"
	"github.com/labstack/echo/v4"
	"github.com/labstack/echo/v4/middleware"
	"google.golang.org/grpc"
)

func HttpServe(l net.Listener, db *pg.DB) error {

	maxMsgSize := 220 * 1024 * 1024
	conn, err := grpc.Dial("localhost:8080", grpc.WithInsecure(), grpc.WithDefaultCallOptions(grpc.MaxCallRecvMsgSize(maxMsgSize), grpc.MaxCallSendMsgSize(maxMsgSize)))
	defer conn.Close()
	if err != nil {
		return err
	}

	//Http Server
	e := echo.New()

	//CORS
	e.Use(middleware.CORSWithConfig(middleware.CORSConfig{
		AllowOrigins:     []string{"*"},
		AllowMethods:     []string{http.MethodGet, http.MethodHead, http.MethodPut, http.MethodPatch, http.MethodPost, http.MethodDelete, http.MethodOptions},
		AllowCredentials: true,
	}))

	//Auth
	e.Use(auth.AuthMiddleware())

	//Clients
	routineClient := pb.NewRoutineAPIClient(conn)
	trainingClient := pb.NewTrainingAPIClient(conn)

	//Publishers
	routinePublisher := publishers.RoutinePublisher{Client: routineClient}
	trainingPublisher := publishers.TrainingPublisher{Client: trainingClient}

	//Repositories
	routineRepository := repositories.PgRoutineRepository{DB: db}
	trainingRepository := repositories.PgTrainingRepository{DB: db}

	//Routes
	HttpRoutineServe(e, routineRepository, &routinePublisher)
	HttpTrainingServe(e, trainingRepository, &trainingPublisher)

	s := &http.Server{Handler: e}
	return s.Serve(l)
}

func WrapControllerHandler(f func(http.ResponseWriter, *http.Request)) func(echo.Context) error {
	return func(c echo.Context) error {
		f(c.Response().Writer, c.Request())
		return nil
	}
}
