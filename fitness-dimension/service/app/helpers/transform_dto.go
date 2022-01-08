package helpers

import (
	"fitness-dimension/core/trainings/training"
	"fitness-dimension/service/app/models"
)

func TranformTrainingToDto(training training.Training) models.Training {
	if training.Video != nil {
		return models.Training{
			ID:          training.ID.Value.String(),
			Name:        training.Name.Value,
			Description: training.Description.Value,
			Categories:  training.Categories.Values,
			TrainerID:   training.TrainerID.Value,
			Video: &models.TrainingVideo{
				ID:   training.Video.ID.Value.String(),
				Name: training.Video.Name.Value,
				Ext:  training.Video.Ext.Value,
				Buff: training.Video.Buff.Value,
			},
		}
	}
	return models.Training{
		ID:          training.ID.Value.String(),
		Name:        training.Name.Value,
		Description: training.Description.Value,
		Categories:  training.Categories.Values,
		TrainerID:   training.TrainerID.Value,
		Video:       nil,
	}
}
