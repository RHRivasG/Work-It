package events

import valuesObjects "fitness-dimension/core/trainings/training/values-objects"

type TrainingVideoUpdated struct {
	ID         valuesObjects.TrainingVideoID
	TrainingID valuesObjects.TrainingID
	Name       valuesObjects.TrainingVideoName
	Ext        valuesObjects.TrainingVideoExt
	Video      valuesObjects.TrainingVideoBuffer
}
