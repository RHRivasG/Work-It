package entities

import (
	"training-service/internal/core/values"

	"github.com/google/uuid"
)

type TrainingVideo struct {
	ID     values.TrainingVideoID
	Name   values.TrainingVideoName
	Ext    values.TrainingVideoExt
	Length values.TrainingVideoLength
	Buff   values.TrainingVideoBuffer
}

func CreateVideo(
	name values.TrainingVideoName,
	ext values.TrainingVideoExt,
	buff values.TrainingVideoBuffer,
) TrainingVideo {

	id := values.TrainingVideoID{Value: uuid.New()}

	v := TrainingVideo{
		ID:     id,
		Name:   name,
		Ext:    ext,
		Buff:   buff,
		Length: values.TrainingVideoLength{Value: len(buff.Value)},
	}

	return v
}

func (v *TrainingVideo) Update(
	name values.TrainingVideoName,
	ext values.TrainingVideoExt,
	buff values.TrainingVideoBuffer,
) {
	v.Name = name
	v.Ext = ext
	v.Buff = buff
}

func (v *TrainingVideo) Destroy() {}
