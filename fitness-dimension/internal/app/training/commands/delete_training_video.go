package commands

import (
	app "fitness-dimension/internal/app/training"

	"github.com/google/uuid"
)

type DeleteTrainingVideo struct {
	app.TrainingCommand
	TrainingID uuid.UUID
}

func (c *DeleteTrainingVideo) Execute(s *app.TrainingService) (interface{}, error) {
	t, err := s.Repository.Get(c.TrainingID.String())
	if err != nil {
		return nil, err
	}

	t.DestroyVideo()
	for _, i := range t.GetEvents() {
		err := s.Publisher.Publish(i)
		if err != nil {
			return nil, err
		}
	}

	return nil, nil
}
