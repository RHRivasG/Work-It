package entities

import (
	valuesObjects "fitness-dimension-core/trainings/training/values-objects"
)

type TrainingVideo struct {
	ID     valuesObjects.TrainingVideoID
	Name   valuesObjects.TrainingVideoName
	Ext    valuesObjects.TrainingVideoExt
	Length valuesObjects.TrainingVideoLength
	Buff   valuesObjects.TrainingVideoVideo
}

func CreateVideo(
	name valuesObjects.TrainingVideoName,
	ext valuesObjects.TrainingVideoExt,
	buff valuesObjects.TrainingVideoVideo,
) TrainingVideo {

	v := TrainingVideo{
		Name: name,
		Ext:  ext,
		Buff: buff,
	}

	return v
}

func (v *TrainingVideo) Update(
	name valuesObjects.TrainingVideoName,
	ext valuesObjects.TrainingVideoExt,
	buff valuesObjects.TrainingVideoVideo,
) {
	v.Name = name
	v.Ext = ext
	v.Buff = buff
}

func (v *TrainingVideo) Destroy() {}
