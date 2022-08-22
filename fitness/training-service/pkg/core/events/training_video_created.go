package events

import "training-service/pkg/core/vo"

type TrainingVideoCreated struct {
	ID         vo.TrainingVideoID
	Name       vo.TrainingVideoName
	Ext        vo.TrainingVideoExt
	Buff       vo.TrainingVideoBuffer
	TrainingID vo.TrainingID
}
