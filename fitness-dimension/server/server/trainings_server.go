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

func HttpTrainingServe(router *mux.Router, r repositories.TrainingRepository) {

	service := trainings.TrainingService{TrainingRepository: r}
	c := controllers.TrainingHttpController{Service: service}

	router.HandleFunc("/training", c.Create).Methods("POST")
	router.HandleFunc("/trainings", c.GetAll).Methods("GET")
	router.HandleFunc("/training/{id}", c.Get).Methods("GET")
	router.HandleFunc("/training/{id}", c.Update).Methods("PUT")
	router.HandleFunc("/training/{id}", c.Delete).Methods("DELETE")

}

func GrpcTrainingServe(s *grpc.Server, db *pg.DB) {
	c := controllers.TrainingApiServer{}
	pb.RegisterTrainingAPIServer(s, &c)
}
