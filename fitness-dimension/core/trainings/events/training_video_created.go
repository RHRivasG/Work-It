package events

import valuesObjects "fitness-dimension/core/trainings/training/values-objects"

type TrainingVideoCreated struct {
	TrainingID valuesObjects.TrainingID
	Name       valuesObjects.TrainingVideoName
	Ext        valuesObjects.TrainingVideoExt
	Video      valuesObjects.TrainingVideoVideo
}
