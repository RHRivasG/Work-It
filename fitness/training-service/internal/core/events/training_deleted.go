package events

import "training-service/internal/core/values"

type TrainingDeleted struct {
	ID values.TrainingID
}
