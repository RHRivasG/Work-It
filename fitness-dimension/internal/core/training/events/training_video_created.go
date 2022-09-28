package events

import "fitness-dimension/internal/core/training/values"

type TrainingVideoCreated struct {
	ID         values.TrainingVideoID
	Name       values.TrainingVideoName
	Ext        values.TrainingVideoExt
	Buff       values.TrainingVideoBuffer
	TrainingID values.TrainingID
}
