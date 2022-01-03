package repositories

import (
	"fitness-dimension/core/trainings/training"

	"github.com/google/uuid"
)

type TrainingRepository interface {
	Find(uuid.UUID) training.Training
	GetAll() []training.Training
}
