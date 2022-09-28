package commands

import (
	"training-service/pkg/application"
	"training-service/pkg/core/vo"

	"github.com/google/uuid"
)

type UpdateTraining struct {
	application.TrainingCommand
	ID          uuid.UUID
	Categories  []string
	TrainerID   string
	Name        string
	Description string
}

func (c *UpdateTraining) Execute(s *application.TrainingService) (interface{}, error) {
	trainerID := vo.TrainerID{Value: c.TrainerID}
	name := vo.TrainingName{Value: c.Name}
	description := vo.TrainingDescription{Value: c.Description}
	categories := vo.TrainingTaxonomies{Values: c.Categories}

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
