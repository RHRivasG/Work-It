package helpers

import (
	"fitness-dimension/application/trainings"
	"fitness-dimension/core/routines/routine"
	"fitness-dimension/core/trainings/training"
	"fitness-dimension/core/trainings/training/entities"
	"fitness-dimension/service/app/models"
)

func TranformTrainingToDto(training training.Training) trainings.TrainingDto {
	if training.Video != nil {
		return trainings.TrainingDto{
			ID:          training.ID.Value.String(),
			Name:        training.Name.Value,
			Description: training.Description.Value,
			Categories:  training.Categories.Values,
			TrainerID:   training.TrainerID.Value,
			Video: &trainings.TrainingVideoDto{
				ID:     training.Video.ID.Value.String(),
				Name:   training.Video.Name.Value,
				Ext:    training.Video.Ext.Value,
				Length: len(training.Video.Buff.Value),
			},
		}
	}
	return trainings.TrainingDto{
		ID:          training.ID.Value.String(),
		Name:        training.Name.Value,
		Description: training.Description.Value,
		Categories:  training.Categories.Values,
		TrainerID:   training.TrainerID.Value,
		Video:       nil,
	}
}

func TransformVideoToDto(video entities.TrainingVideo) models.TrainingVideo {
	return models.TrainingVideo{
		ID:     video.ID.Value.String(),
		Name:   video.Name.Value,
		Ext:    video.Ext.Value,
		Length: len(video.Buff.Value),
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
