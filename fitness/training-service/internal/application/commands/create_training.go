package commands

import (
	"training-service/internal/application"
	core "training-service/internal/core"
	"training-service/internal/core/values"
)

type CreateTraining struct {
	application.TrainingCommand
	Categories  []string
	TrainerID   string
	Name        string
	Description string
}

func (c *CreateTraining) Execute(s *application.TrainingService) (interface{}, error) {

	trainerID := values.TrainerID{Value: c.TrainerID}
	name := values.TrainingName{Value: c.Name}
	description := values.TrainingDescription{Value: c.Description}
	categories := values.TrainingTaxonomies{Values: c.Categories}

	t, _ := core.CreateTraining(categories, trainerID, name, description)
	for _, i := range t.GetEvents() {
		s.Publisher.Publish(i)
	}

	return t.ID.Value.String(), nil
}
