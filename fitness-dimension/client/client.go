package main

import (
	"context"
	pb "fitness-dimension/gen/proto"
	"fmt"
	"log"

	"google.golang.org/grpc"
)

func main() {
	conn, err := grpc.Dial("localhost:8080", grpc.WithInsecure())
	defer conn.Close()
	if err != nil {
		log.Fatal(err)
	}

	client := pb.NewRoutineAPIClient(conn)

	res, err := client.Save(context.Background(), &pb.RoutineCreated{
		Id:          "1",
		Name:        "Week 1",
		UserId:      "3",
		Description: "Routine for the week 1",
		TrainingsId: []string{"1", "2", "3"},
	})
	/*
		res, err = client.Update(context.Background(), &pb.RoutineUpdated{
			Id:          "1",
			Name:        "Week 2",
			UserId:      "3",
			Description: "Routine for the week 2",
			TrainingsId: []string{"1", "2", "3"},
		})
		res, err = client.Delete(context.Background(), &pb.RoutineDeleted{Id: "1"})
		if err != nil {
			log.Fatal(err)
		}
		res, err = client.AddTraining(context.Background(), &pb.TrainingAdded{
			RoutineId:  "1",
			TrainingId: "1",
		})
		res, err = client.RemoveTraining(context.Background(), &pb.TrainingRemoved{
			RoutineId:  "1",
			TrainingId: "1",
		})
	*/
	fmt.Println(res)
}
