package events

import "training-service/pkg/core/vo"

type TrainingDeleted struct {
	ID vo.TrainingID
}
