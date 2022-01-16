package app

import (
	"fitness-dimension/internal/core/training"
	"fitness-dimension/internal/core/training/entities"
)

type TrainingRepository interface {
	Get(id string) (*training.Training, error)
	GetAll() ([]training.Training, error)
	GetByTrainer(id string) ([]training.Training, error)
	GetVideo(id string) *entities.TrainingVideo
	GetVideoMetadata(id string) *entities.TrainingVideo
}
