package events

import "fitness-dimension/internal/core/training/values"

type TrainingDeleted struct {
	ID values.TrainingID
}
