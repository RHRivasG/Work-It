package events

import valuesObjects "fitness-dimension/trainings/training/values-objects"

type TrainingVideoUpdated struct {
	ID    valuesObjects.TrainingVideoID
	Name  valuesObjects.TrainingVideoName
	ext   valuesObjects.TrainingVideoExt
	video valuesObjects.TrainingVideoVideo
}
