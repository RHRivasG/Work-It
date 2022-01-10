package trainings

import (
	"fitness-dimension/core/trainings/training"
	"fitness-dimension/core/trainings/training/entities"
)

type TrainingRepository interface {
	Get(id string) (*training.Training, error)
	GetAll() ([]training.Training, error)
	GetByTrainer(id string) ([]training.Training, error)
	GetVideo(id string) *entities.TrainingVideo
}
