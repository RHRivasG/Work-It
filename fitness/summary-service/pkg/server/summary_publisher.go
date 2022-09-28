package server

import (
	"context"
	"log"
	"summary-service/internal/core/events"
	pb "summary-service/pkg/api/proto"
)

type SummaryPublisher struct {
	Client pb.SummaryAPIClient
}

func (p SummaryPublisher) Publish(event interface{}) {
	switch event.(type) {
	case events.SummaryCreated:
		p.publishSummaryCreated(event.(events.SummaryCreated))
	case events.SummaryUpdated:
		p.publishSummaryUpdated(event.(events.SummaryUpdated))
	}

}

func (p SummaryPublisher) publishSummaryCreated(event events.SummaryCreated) {

	res, err := p.Client.Save(context.Background(), &pb.SummaryCreated{
		Id:        event.ID.Value().String(),
		RoutineId: event.Routine.Value().String(),
	})

	if err != nil {
		log.Fatal(err)
	}

	log.Println(res)
}

func (p SummaryPublisher) publishSummaryUpdated(event events.SummaryUpdated) {

	res, err := p.Client.Update(context.Background(), &pb.SummaryUpdated{
		RoutineId: event.Routine.Value().String(),
		Mintime:   event.MinTime.Value().String(),
		Maxtime:   event.MaxTime.Value().String(),
	})

	if err != nil {
		log.Fatal(err)
	}

	log.Println(res)
}
