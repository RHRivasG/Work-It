package server

import (
	"context"
	"fmt"
	"log"
	pb "training-service/pkg/api/proto"
	"training-service/pkg/application"
	"training-service/pkg/core/events"
)

type TrainingPublisher struct {
	application.TrainingPublisher
	Client pb.TrainingAPIClient
}

func (p *TrainingPublisher) Publish(e interface{}) error {
	switch e.(type) {
	case events.TrainingCreated:
		event := e.(events.TrainingCreated)
		return p.publishTrainingCreated(event)

	case events.TrainingUpdated:
		event := e.(events.TrainingUpdated)
		return p.publishTrainingUpdated(event)

	case events.TrainingDeleted:
		event := e.(events.TrainingDeleted)
		return p.publishTrainingDeleted(event)

	case events.TrainingVideoCreated:
		event := e.(events.TrainingVideoCreated)
		return p.publishTrainingVideoCreated(event)

	case events.TrainingVideoDeleted:
		event := e.(events.TrainingVideoDeleted)
		return p.publishTrainingVideoDeleted(event)

	}

	return nil
}

func (p *TrainingPublisher) publishTrainingCreated(event events.TrainingCreated) error {
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
	return nil
}

func (p *TrainingPublisher) publishTrainingUpdated(event events.TrainingUpdated) error {
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
	return nil
}

func (p *TrainingPublisher) publishTrainingDeleted(event events.TrainingDeleted) error {
	res, err := p.Client.Delete(context.Background(), &pb.TrainingDeleted{
		Id: event.ID.Value.String(),
	})

	if err != nil {
		log.Fatal(err)
		return nil
	}

	fmt.Println(res)
	return nil
}

func (p *TrainingPublisher) publishTrainingVideoCreated(event events.TrainingVideoCreated) error {
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
	return nil
}

func (p *TrainingPublisher) publishTrainingVideoDeleted(event events.TrainingVideoDeleted) error {
	res, err := p.Client.DeleteVideo(context.Background(), &pb.TrainingVideoDeleted{
		Id: event.ID.Value.String(),
	})

	if err != nil {
		log.Fatal(err)
		return err
	}

	fmt.Println(res)
	return nil
}
