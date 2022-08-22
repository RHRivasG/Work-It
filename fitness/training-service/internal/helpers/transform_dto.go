package helpers

import (
	"training-service/pkg/application"
	"training-service/pkg/core"
	"training-service/pkg/core/entities"
)

func TranformTrainingToDto(training *core.Training) application.TrainingDto {
	if training.Video != nil {
		return application.TrainingDto{
			ID:          training.ID.Value.String(),
			Name:        training.Name.Value,
			Description: training.Description.Value,
			Categories:  training.Categories.Values,
			TrainerID:   training.TrainerID.Value,
			Video: &application.TrainingVideoDto{
				ID:     training.Video.ID.Value.String(),
				Name:   training.Video.Name.Value,
				Ext:    training.Video.Ext.Value,
				Length: len(training.Video.Buff.Value),
			},
		}
	}
	return application.TrainingDto{
		ID:          training.ID.Value.String(),
		Name:        training.Name.Value,
		Description: training.Description.Value,
		Categories:  training.Categories.Values,
		TrainerID:   training.TrainerID.Value,
		Video:       nil,
	}
}

func TransformVideoToDto(video *entities.TrainingVideo) application.TrainingVideoDto {
	return application.TrainingVideoDto{
		ID:     video.ID.Value.String(),
		Name:   video.Name.Value,
		Ext:    video.Ext.Value,
		Length: video.Length.Value,
	}
}
