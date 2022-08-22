package commands

import (
	"training-service/pkg/application"

	"github.com/google/uuid"
)

type DeleteTrainingVideo struct {
	application.TrainingCommand
	TrainingID uuid.UUID
}

func (c *DeleteTrainingVideo) Execute(s *application.TrainingService) (interface{}, error) {
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
