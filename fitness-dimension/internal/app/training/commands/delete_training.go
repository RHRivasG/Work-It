package commands

import (
	app "fitness-dimension/internal/app/training"

	"github.com/google/uuid"
)

type DeleteTraining struct {
	app.TrainingCommand
	ID uuid.UUID
}

func (c *DeleteTraining) Execute(s *app.TrainingService) (interface{}, error) {

	t, err := s.Repository.Get(c.ID.String())
	if err != nil {
		return nil, err
	}

	t.Destroy()
	for _, i := range t.GetEvents() {
		err := s.Publisher.Publish(i)
		if err != nil {
			return nil, err
		}
	}

	return nil, nil
}
