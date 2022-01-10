package repositories

import (
	"fitness-dimension/core/trainings/training/entities"
)

type TrainingVideoRepository interface {
	Find(id string) entities.TrainingVideo
}
