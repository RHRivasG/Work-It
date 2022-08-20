package events

import "training-service/internal/core/values"

type TrainingUpdated struct {
	ID          values.TrainingID
	Categories  values.TrainingTaxonomies
	TrainerID   values.TrainerID
	Name        values.TrainingName
	Description values.TrainingDescription
}
