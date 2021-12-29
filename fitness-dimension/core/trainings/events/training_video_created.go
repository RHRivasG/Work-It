package events

import valuesObjects "fitness-dimension/core/trainings/training/values-objects"

type TrainingVideoCreated struct {
	ID         valuesObjects.TrainingVideoID
	Name       valuesObjects.TrainingVideoName
	Ext        valuesObjects.TrainingVideoExt
	Buff       valuesObjects.TrainingVideoBuffer
	TrainingID valuesObjects.TrainingID
}
