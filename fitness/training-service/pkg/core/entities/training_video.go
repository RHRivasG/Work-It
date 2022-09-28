package entities

import (
	"training-service/pkg/core/vo"

	"github.com/google/uuid"
)

type TrainingVideo struct {
	ID     vo.TrainingVideoID
	Name   vo.TrainingVideoName
	Ext    vo.TrainingVideoExt
	Length vo.TrainingVideoLength
	Buff   vo.TrainingVideoBuffer
}

func CreateVideo(
	name vo.TrainingVideoName,
	ext vo.TrainingVideoExt,
	buff vo.TrainingVideoBuffer,
) TrainingVideo {

	id := vo.TrainingVideoID{Value: uuid.New()}

	v := TrainingVideo{
		ID:     id,
		Name:   name,
		Ext:    ext,
		Buff:   buff,
		Length: vo.TrainingVideoLength{Value: len(buff.Value)},
	}

	return v
}

func (v *TrainingVideo) Update(
	name vo.TrainingVideoName,
	ext vo.TrainingVideoExt,
	buff vo.TrainingVideoBuffer,
) {
	v.Name = name
	v.Ext = ext
	v.Buff = buff
}

func (v *TrainingVideo) Destroy() {}
