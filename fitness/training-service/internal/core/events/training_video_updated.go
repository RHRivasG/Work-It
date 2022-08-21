package events

import "training-service/internal/core/vo"

type TrainingVideoUpdated struct {
	ID         vo.TrainingVideoID
	TrainingID vo.TrainingID
	Name       vo.TrainingVideoName
	Ext        vo.TrainingVideoExt
	Video      vo.TrainingVideoBuffer
}
