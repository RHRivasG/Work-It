package entities

import (
	valuesObjects "fitness-dimension/core/trainings/training/values-objects"

	"github.com/google/uuid"
)

type TrainingVideo struct {
	ID     valuesObjects.TrainingVideoID
	Name   valuesObjects.TrainingVideoName
	Ext    valuesObjects.TrainingVideoExt
	Length valuesObjects.TrainingVideoLength
	Buff   valuesObjects.TrainingVideoBuffer
}

func CreateVideo(
	name valuesObjects.TrainingVideoName,
	ext valuesObjects.TrainingVideoExt,
	buff valuesObjects.TrainingVideoBuffer,
) TrainingVideo {

	id := valuesObjects.TrainingVideoID{Value: uuid.New()}

	v := TrainingVideo{
		ID:   id,
		Name: name,
		Ext:  ext,
		Buff: buff,
	}

	return v
}

func (v *TrainingVideo) Update(
	name valuesObjects.TrainingVideoName,
	ext valuesObjects.TrainingVideoExt,
	buff valuesObjects.TrainingVideoBuffer,
) {
	v.Name = name
	v.Ext = ext
	v.Buff = buff
}

func (v *TrainingVideo) Destroy() {}
