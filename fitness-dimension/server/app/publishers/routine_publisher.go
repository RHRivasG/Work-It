package publishers

import (
	"context"
	"fitness-dimension/application/routines"
	"fitness-dimension/core/routines/events"
	pb "fitness-dimension/gen/proto"
	"fmt"
	"log"
)

type RoutinePublisher struct {
	routines.RoutinePublisher
	Client pb.RoutineAPIClient
}

func (p *RoutinePublisher) Publish(e interface{}) {
	switch e.(type) {
	case events.RoutineCreated:
		event := e.(events.RoutineCreated)
		res, err := p.Client.Save(context.Background(), &pb.RoutineCreated{
			Id:          event.ID.Value.String(),
			Name:        event.Name.Value,
			UserId:      event.UserID.Value,
			Description: event.Description.Value,
		})

		if err != nil {
			log.Fatal(err)
		}

		fmt.Println(res)
	}
}
