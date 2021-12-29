package server

import (
	pb "fitness-dimension/gen/proto"
	"fitness-dimension/server/app/publishers"
	"fitness-dimension/server/app/repositories"

	"net"
	"net/http"

	"github.com/go-pg/pg/v10"
	"github.com/gorilla/mux"
	"google.golang.org/grpc"
)

func HttpServe(l net.Listener, db *pg.DB) error {

	conn, err := grpc.Dial("localhost:8080", grpc.WithInsecure())
	defer conn.Close()
	if err != nil {
		return err
	}

	router := mux.NewRouter().StrictSlash(true)

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
	HttpRoutineServe(router, routineRepository, &routinePublisher)
	HttpTrainingServe(router, trainingRepository, &trainingPublisher)

	s := &http.Server{Handler: router}
	return s.Serve(l)
}
