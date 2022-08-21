package events

import "training-service/internal/core/vo"

type TrainingVideoDeleted struct {
	ID vo.TrainingVideoID
}
