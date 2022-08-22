package events

import "training-service/pkg/core/vo"

type TrainingCreated struct {
	ID          vo.TrainingID
	Categories  vo.TrainingTaxonomies
	TrainerID   vo.TrainerID
	Name        vo.TrainingName
	Description vo.TrainingDescription
}
