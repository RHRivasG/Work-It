package repositories

import (
	"fitness-dimension/core/trainings/training/entities"

	"github.com/google/uuid"
)

type TrainingVideoRepository interface {
	Find(uuid.UUID) entities.TrainingVideo
	Delete(uuid.UUID)
}
