package server

import (
	"fitness-dimension/application/trainings"
	"fitness-dimension/application/trainings/repositories"
	pb "fitness-dimension/gen/proto"
	"fitness-dimension/server/app/controllers"

	"github.com/go-pg/pg/v10"
	"github.com/gorilla/mux"
	"google.golang.org/grpc"
)

func HttpTrainingServe(router *mux.Router, r repositories.TrainingRepository, p trainings.TrainingPublisher) {

	service := trainings.TrainingService{Repository: r, Publisher: p}
	c := controllers.TrainingHttpController{Service: service}

	router.HandleFunc("/trainings", c.Create).Methods("POST")
	router.HandleFunc("/trainings", c.GetAll).Methods("GET")
	router.HandleFunc("/trainings/{id}", c.Get).Methods("GET")
	router.HandleFunc("/trainings/{id}", c.Update).Methods("PUT")
	router.HandleFunc("/trainings/{id}", c.Delete).Methods("DELETE")

}

func GrpcTrainingServe(s *grpc.Server, db *pg.DB) {
	c := controllers.TrainingApiServer{}
	pb.RegisterTrainingAPIServer(s, &c)
}
