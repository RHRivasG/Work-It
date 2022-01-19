package commands

import (
	app "fitness-dimension/internal/app/routine"
	"fitness-dimension/internal/core/routine/values"

	"github.com/google/uuid"
)

type AddRoutineTraining struct {
	app.RoutineCommand
	ID         uuid.UUID
	TrainingID uuid.UUID
}

func (c *AddRoutineTraining) Execute(s *app.RoutineService) (interface{}, error) {
	trainingID, err := values.NewRoutineTrainingID(c.TrainingID)
	if err != nil {
		return nil, err
	}

	r, err := s.Repository.Find(c.ID)
	if err != nil {
		return nil, err
	}

	r.AddTraining(trainingID)
	for _, i := range r.GetEvents() {
		s.Publisher.Publish(i)
	}

	return nil, nil
}
