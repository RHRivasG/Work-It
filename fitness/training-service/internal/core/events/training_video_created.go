package events

import "training-service/internal/core/values"

type TrainingVideoCreated struct {
	ID         values.TrainingVideoID
	Name       values.TrainingVideoName
	Ext        values.TrainingVideoExt
	Buff       values.TrainingVideoBuffer
	TrainingID values.TrainingID
}
