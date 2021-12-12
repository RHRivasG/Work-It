package events

import valuesObjects "fitness-dimension-core/trainings/training/values-objects"

type TrainingVideoUpdated struct {
	ID    valuesObjects.TrainingVideoID
	Name  valuesObjects.TrainingVideoName
	Ext   valuesObjects.TrainingVideoExt
	Video valuesObjects.TrainingVideoVideo
}
