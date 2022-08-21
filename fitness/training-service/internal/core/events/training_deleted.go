package events

import "training-service/internal/core/vo"

type TrainingDeleted struct {
	ID vo.TrainingID
}
