package events

import "training-service/internal/core/values"

type TrainingVideoDeleted struct {
	ID values.TrainingVideoID
}
