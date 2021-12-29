package publishers

import (
	"context"
	"fitness-dimension/application/trainings"
	"fitness-dimension/core/trainings/events"
	pb "fitness-dimension/gen/proto"
	"fmt"
	"log"
)

type TrainingPublisher struct {
	trainings.TrainingPublisher
	Client pb.TrainingAPIClient
}

func (p *TrainingPublisher) Publish(e interface{}) {
	switch e.(type) {
	case events.TrainingCreated:
		event := e.(events.TrainingCreated)
		res, err := p.Client.Save(context.Background(), &pb.TrainingCreated{
			TrainerId:   event.TrainerID.Value,
			Name:        event.Name.Value,
			Description: event.Description.Value,
		})

		if err != nil {
			log.Fatal(err)
		}

		fmt.Println(res)
	case events.TrainingUpdated:
		event := e.(events.TrainingUpdated)
		res, err := p.Client.Update(context.Background(), &pb.TrainingUpdated{
			Id:          event.ID.Value.String(),
			TrainerId:   event.TrainerID.Value,
			Name:        event.Name.Value,
			Description: event.Description.Value,
		})

		if err != nil {
			log.Fatal(err)
		}

		fmt.Println(res)
	case events.TrainingDeleted:
		event := e.(events.TrainingDeleted)
		res, err := p.Client.Delete(context.Background(), &pb.TrainingDeleted{
			Id: event.ID.Value.String(),
		})

		if err != nil {
			log.Fatal(err)
		}

		fmt.Println(res)
	case events.TrainingVideoCreated:
		event := e.(events.TrainingVideoCreated)
		res, err := p.Client.SaveVideo(context.Background(), &pb.TrainingVideoCreated{
			TrainingId: event.TrainingID.Value.String(),
			Name:       event.Name.Value,
			Ext:        event.Ext.Value,
		})

		if err != nil {
			log.Fatal(err)
		}

		fmt.Println(res)
	case events.TrainingVideoUpdated:
		event := e.(events.TrainingVideoUpdated)
		res, err := p.Client.UpdateVideo(context.Background(), &pb.TrainingVideoUpdated{
			Id:         event.ID.Value.String(),
			TrainingId: event.TrainingID.Value.String(),
			Name:       event.Name.Value,
			Ext:        event.Ext.Value,
		})

		if err != nil {
			log.Fatal(err)
		}

		fmt.Println(res)
	case events.TrainingVideoDeleted:
		event := e.(events.TrainingVideoDeleted)
		res, err := p.Client.DeleteVideo(context.Background(), &pb.TrainingVideoDeleted{
			Id: event.ID.Value.String(),
		})

		if err != nil {
			log.Fatal(err)
		}

		fmt.Println(res)

	}
}
