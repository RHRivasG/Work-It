package helpers

import (
	"fitness-dimension/core/routines/routine"
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

func TranformRoutineToDto(routine routine.Routine) models.Routine {

	var trainingsId []string
	for _, tId := range routine.TrainingsID.Values {
		trainingsId = append(trainingsId, tId.String())
	}

	return models.Routine{
		ID:          routine.ID.Value.String(),
		Name:        routine.Name.Value,
		UserID:      routine.UserID.Value,
		Description: routine.Description.Value,
		Trainings:   trainingsId,
	}
}
