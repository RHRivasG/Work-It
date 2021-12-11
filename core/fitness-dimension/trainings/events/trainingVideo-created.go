package events

import valuesObjects "fitness-dimension/trainings/training/values-objects"

type TrainingVideoCreated struct {
	ID    valuesObjects.TrainingVideoID
	Name  valuesObjects.TrainingVideoName
	ext   valuesObjects.TrainingVideoExt
	video valuesObjects.TrainingVideoVideo
}
