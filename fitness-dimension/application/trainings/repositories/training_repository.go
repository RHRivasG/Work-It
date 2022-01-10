package repositories

import (
	"fitness-dimension/core/trainings/training"
	"fitness-dimension/core/trainings/training/entities"

	"github.com/google/uuid"
)

type TrainingRepository interface {
	Find(uuid.UUID) training.Training
	GetAll() []training.Training
	GetByTrainer(id string) []training.Training
	GetVideo(id string) *entities.TrainingVideo
}
