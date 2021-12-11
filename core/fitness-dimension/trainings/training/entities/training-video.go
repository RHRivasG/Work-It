package entities

import (
	valuesObjects "fitness-dimension/trainings/training/values-objects"
)

type TrainingVideo struct {
	ID     valuesObjects.TrainingVideoID
	Name   valuesObjects.TrainingVideoName
	Ext    valuesObjects.TrainingVideoExt
	lenght valuesObjects.TrainingVideoLength
}

func create() {}

func (t *TrainingVideo) update() {}

func (t *TrainingVideo) destroy() {}
