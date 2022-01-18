package training

import (
	"context"
	app "fitness-dimension/internal/app/training"
	"fitness-dimension/internal/core/training/events"
	pb "fitness-dimension/pkg/api/proto"
	"fmt"
	"log"
)

type TrainingPublisher struct {
	app.TrainingPublisher
	Client pb.TrainingAPIClient
}

func (p *TrainingPublisher) Publish(e interface{}) error {
	switch e.(type) {
	case events.TrainingCreated:
		event := e.(events.TrainingCreated)
		res, err := p.Client.Save(context.Background(), &pb.TrainingCreated{
			Id:          event.ID.Value.String(),
			TrainerId:   event.TrainerID.Value,
			Name:        event.Name.Value,
			Description: event.Description.Value,
			Categories:  event.Categories.Values,
		})

		if err != nil {
			log.Fatal(err)
			return err
		}

		fmt.Println(res)
	case events.TrainingUpdated:
		event := e.(events.TrainingUpdated)
		res, err := p.Client.Update(context.Background(), &pb.TrainingUpdated{
			Id:          event.ID.Value.String(),
			TrainerId:   event.TrainerID.Value,
			Name:        event.Name.Value,
			Description: event.Description.Value,
			Categories:  event.Categories.Values,
		})

		if err != nil {
			log.Fatal(err)
			return err
		}

		fmt.Println(res)
	case events.TrainingDeleted:
		event := e.(events.TrainingDeleted)
		res, err := p.Client.Delete(context.Background(), &pb.TrainingDeleted{
			Id: event.ID.Value.String(),
		})

		if err != nil {
			log.Fatal(err)
			return nil
		}

		fmt.Println(res)
	case events.TrainingVideoCreated:
		event := e.(events.TrainingVideoCreated)
		res, err := p.Client.SaveVideo(context.Background(), &pb.TrainingVideoCreated{
			Id:         event.ID.Value.String(),
			TrainingId: event.TrainingID.Value.String(),
			Name:       event.Name.Value,
			Ext:        event.Ext.Value,
			Video:      event.Buff.Value,
		})

		if err != nil {
			log.Fatal(err)
			return err
		}

		fmt.Println(res)
	case events.TrainingVideoDeleted:
		event := e.(events.TrainingVideoDeleted)
		res, err := p.Client.DeleteVideo(context.Background(), &pb.TrainingVideoDeleted{
			Id: event.ID.Value.String(),
		})

		if err != nil {
			log.Fatal(err)
			return err
		}

		fmt.Println(res)

	}
	return nil
}
