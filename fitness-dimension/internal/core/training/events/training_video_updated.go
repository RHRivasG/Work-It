package events

import "fitness-dimension/internal/core/training/values"

type TrainingVideoUpdated struct {
	ID         values.TrainingVideoID
	TrainingID values.TrainingID
	Name       values.TrainingVideoName
	Ext        values.TrainingVideoExt
	Video      values.TrainingVideoBuffer
}
