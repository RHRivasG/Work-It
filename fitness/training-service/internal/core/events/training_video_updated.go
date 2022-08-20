package events

import "training-service/internal/core/values"

type TrainingVideoUpdated struct {
	ID         values.TrainingVideoID
	TrainingID values.TrainingID
	Name       values.TrainingVideoName
	Ext        values.TrainingVideoExt
	Video      values.TrainingVideoBuffer
}
