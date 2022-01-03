package server

import (
	"fitness-dimension/application/routines"
	"fitness-dimension/application/routines/repositories"
	"fitness-dimension/server/app/controllers"

	"github.com/go-pg/pg/v10"
	"github.com/gorilla/mux"
	"google.golang.org/grpc"

	pb "fitness-dimension/gen/proto"
)

func HttpRoutineServe(router *mux.Router, r repositories.RoutineRepository, p routines.RoutinePublisher) {

	service := routines.RoutineService{Repository: r, Publisher: p}
	c := controllers.RoutineHttpController{Service: service}

	router.HandleFunc("/routines", c.Create).Methods("POST")
	router.HandleFunc("/routines", c.GetAll).Methods("GET")
	router.HandleFunc("/routines/{id}", c.Get).Methods("GET")
	router.HandleFunc("/routines/{id}", c.Update).Methods("PUT")
	router.HandleFunc("/routines/{id}", c.Delete).Methods("DELETE")
}

func GrpcRoutineServe(s *grpc.Server, db *pg.DB) {
	c := controllers.RoutineApiServer{DB: db}
	pb.RegisterRoutineAPIServer(s, &c)
}

func GrpcRoutineClient(conn *grpc.ClientConn) pb.RoutineAPIClient {
	return pb.NewRoutineAPIClient(conn)
}
