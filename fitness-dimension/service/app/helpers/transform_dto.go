package helpers

import (
	"fitness-dimension/application/routines"
	"fitness-dimension/application/trainings"
	"fitness-dimension/core/routine"
	"fitness-dimension/core/trainings/training"
	"fitness-dimension/core/trainings/training/entities"
)

func TranformTrainingToDto(training *training.Training) trainings.TrainingDto {
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

func TransformVideoToDto(video *entities.TrainingVideo) trainings.TrainingVideoDto {
	return trainings.TrainingVideoDto{
		ID:     video.ID.Value.String(),
		Name:   video.Name.Value,
		Ext:    video.Ext.Value,
		Length: video.Length.Value,
	}
}

func TranformRoutineToDto(routine *routine.Routine) routines.RoutineDto {

	var trainingsId []string
	for _, tId := range routine.TrainingsID.Values() {
		trainingsId = append(trainingsId, tId.String())
	}

	return routines.RoutineDto{
		ID:          routine.ID.Value().String(),
		Name:        routine.Name.Value(),
		UserID:      routine.UserID.Value(),
		Description: routine.Description.Value(),
		TrainingsID: trainingsId,
	}
}
