package commands

import (
	app "fitness-dimension/internal/app/training"
	"fitness-dimension/internal/core/training/values"

	"github.com/google/uuid"
)

type UpdateTraining struct {
	app.TrainingCommand
	ID          uuid.UUID
	Categories  []string
	TrainerID   string
	Name        string
	Description string
}

func (c *UpdateTraining) Execute(s *app.TrainingService) (interface{}, error) {
	trainerID := values.TrainerID{Value: c.TrainerID}
	name := values.TrainingName{Value: c.Name}
	description := values.TrainingDescription{Value: c.Description}
	categories := values.TrainingTaxonomies{Values: c.Categories}

	t, err := s.Repository.Get(c.ID.String())
	if err != nil {
		return nil, err
	}

	t.Update(categories, trainerID, name, description)
	for _, i := range t.GetEvents() {
		err := s.Publisher.Publish(i)
		if err != nil {
			return nil, err
		}
	}

	return nil, nil
}
