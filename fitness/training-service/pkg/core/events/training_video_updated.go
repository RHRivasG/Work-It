package events

import "training-service/pkg/core/vo"

type TrainingVideoUpdated struct {
	ID         vo.TrainingVideoID
	TrainingID vo.TrainingID
	Name       vo.TrainingVideoName
	Ext        vo.TrainingVideoExt
	Video      vo.TrainingVideoBuffer
}
