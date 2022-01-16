package helpers

import (
	approutine "fitness-dimension/internal/app/routine"
	apptraining "fitness-dimension/internal/app/training"
	"fitness-dimension/internal/core/routine"
	"fitness-dimension/internal/core/training"
	"fitness-dimension/internal/core/training/entities"
)

func TranformTrainingToDto(training *training.Training) apptraining.TrainingDto {
	if training.Video != nil {
		return apptraining.TrainingDto{
			ID:          training.ID.Value.String(),
			Name:        training.Name.Value,
			Description: training.Description.Value,
			Categories:  training.Categories.Values,
			TrainerID:   training.TrainerID.Value,
			Video: &apptraining.TrainingVideoDto{
				ID:     training.Video.ID.Value.String(),
				Name:   training.Video.Name.Value,
				Ext:    training.Video.Ext.Value,
				Length: len(training.Video.Buff.Value),
			},
		}
	}
	return apptraining.TrainingDto{
		ID:          training.ID.Value.String(),
		Name:        training.Name.Value,
		Description: training.Description.Value,
		Categories:  training.Categories.Values,
		TrainerID:   training.TrainerID.Value,
		Video:       nil,
	}
}

func TransformVideoToDto(video *entities.TrainingVideo) apptraining.TrainingVideoDto {
	return apptraining.TrainingVideoDto{
		ID:     video.ID.Value.String(),
		Name:   video.Name.Value,
		Ext:    video.Ext.Value,
		Length: video.Length.Value,
	}
}

func TranformRoutineToDto(routine *routine.Routine) approutine.RoutineDto {

	var trainingsId []string
	for _, tId := range routine.TrainingsID.Values() {
		trainingsId = append(trainingsId, tId.String())
	}

	return approutine.RoutineDto{
		ID:          routine.ID.Value().String(),
		Name:        routine.Name.Value(),
		UserID:      routine.UserID.Value(),
		Description: routine.Description.Value(),
		TrainingsID: trainingsId,
	}
}
