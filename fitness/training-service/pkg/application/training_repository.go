package application

import (
	"training-service/pkg/core"
	"training-service/pkg/core/entities"
)

type TrainingRepository interface {
	Get(id string) (*core.Training, error)
	GetAll() ([]core.Training, error)
	GetByTrainer(id string) ([]core.Training, error)
	GetVideo(id string) *entities.TrainingVideo
	GetVideoMetadata(id string) *entities.TrainingVideo
}
