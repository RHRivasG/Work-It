package routine

import (
	"context"
	app "fitness-dimension/internal/app/routine"
	"fitness-dimension/internal/core/routine/events"
	pb "fitness-dimension/pkg/api/proto"
	"fmt"
	"log"
)

type RoutinePublisher struct {
	app.RoutinePublisher
	Client pb.RoutineAPIClient
}

func (p *RoutinePublisher) Publish(e interface{}) {
	switch e.(type) {
	case events.RoutineCreated:
		event := e.(events.RoutineCreated)
		p.publishRoutineCreated(event)

	case events.RoutineUpdated:
		event := e.(events.RoutineUpdated)
		p.publishRoutineUpdated(event)

	case events.RoutineDeleted:
		event := e.(events.RoutineDeleted)
		p.publishRoutineDeleted(event)

	case events.TrainingAdded:
		event := e.(events.TrainingAdded)
		p.publishTrainingAdded(event)

	case events.TrainingRemoved:
		event := e.(events.TrainingRemoved)
		p.publishTrainingRemoved(event)

	}

}

func (p *RoutinePublisher) publishRoutineCreated(event events.RoutineCreated) {
	var trainings []string
	for _, t := range event.TrainingsID.Values() {
		trainings = append(trainings, t.String())
	}

	res, err := p.Client.Save(context.Background(), &pb.RoutineCreated{
		Id:          event.ID.Value().String(),
		Name:        event.Name.Value(),
		UserId:      event.UserID.Value(),
		Description: event.Description.Value(),
		TrainingsId: trainings,
	})

	if err != nil {
		log.Fatal(err)
	}

	fmt.Println(res)
}

func (p *RoutinePublisher) publishRoutineUpdated(event events.RoutineUpdated) {
	var trainings []string
	for _, t := range event.TrainingsID.Values() {
		trainings = append(trainings, t.String())
	}

	res, err := p.Client.Update(context.Background(), &pb.RoutineUpdated{
		Id:          event.ID.Value().String(),
		Name:        event.Name.Value(),
		UserId:      event.UserID.Value(),
		Description: event.Description.Value(),
		TrainingsId: trainings,
	})

	if err != nil {
		log.Fatal(err)
	}

	fmt.Println(res)
}

func (p *RoutinePublisher) publishRoutineDeleted(event events.RoutineDeleted) {
	res, err := p.Client.Delete(context.Background(), &pb.RoutineDeleted{
		Id: event.ID.Value().String(),
	})

	if err != nil {
		log.Fatal(err)
	}

	fmt.Println(res)
}

func (p *RoutinePublisher) publishTrainingAdded(event events.TrainingAdded) {
	res, err := p.Client.AddTraining(context.Background(), &pb.TrainingAdded{
		RoutineId:  event.ID.Value().String(),
		TrainingId: event.TrainingID.Value().String(),
		Order:      int32(event.Order.Value()),
	})

	if err != nil {
		log.Fatal(err)
	}

	fmt.Println(res)
}

func (p *RoutinePublisher) publishTrainingRemoved(event events.TrainingRemoved) {
	res, err := p.Client.RemoveTraining(context.Background(), &pb.TrainingRemoved{
		RoutineId:  event.ID.Value().String(),
		TrainingId: event.TrainingID.Value().String(),
	})

	if err != nil {
		log.Fatal(err)
	}

	fmt.Println(res)
}
