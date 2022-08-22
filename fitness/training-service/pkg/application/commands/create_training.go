package commands

import (
	"training-service/pkg/application"
	core "training-service/pkg/core"
	"training-service/pkg/core/vo"
)

type CreateTraining struct {
	application.TrainingCommand
	Categories  []string
	TrainerID   string
	Name        string
	Description string
}

func (c *CreateTraining) Execute(s *application.TrainingService) (interface{}, error) {

	trainerID := vo.TrainerID{Value: c.TrainerID}
	name := vo.TrainingName{Value: c.Name}
	description := vo.TrainingDescription{Value: c.Description}
	categories := vo.TrainingTaxonomies{Values: c.Categories}

	t, _ := core.CreateTraining(categories, trainerID, name, description)
	for _, i := range t.GetEvents() {
		s.Publisher.Publish(i)
	}

	return t.ID.Value.String(), nil
}
