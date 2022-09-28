package commands

import (
	app "fitness-dimension/internal/app/training"
	"fitness-dimension/internal/core/training"
	"fitness-dimension/internal/core/training/values"
)

type CreateTraining struct {
	app.TrainingCommand
	Categories  []string
	TrainerID   string
	Name        string
	Description string
}

func (c *CreateTraining) Execute(s *app.TrainingService) (interface{}, error) {

	trainerID := values.TrainerID{Value: c.TrainerID}
	name := values.TrainingName{Value: c.Name}
	description := values.TrainingDescription{Value: c.Description}
	categories := values.TrainingTaxonomies{Values: c.Categories}

	t, _ := training.CreateTraining(categories, trainerID, name, description)
	for _, i := range t.GetEvents() {
		s.Publisher.Publish(i)
	}

	return t.ID.Value.String(), nil
}
