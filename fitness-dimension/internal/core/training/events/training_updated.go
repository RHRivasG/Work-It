package events

import "fitness-dimension/internal/core/training/values"

type TrainingUpdated struct {
	ID          values.TrainingID
	Categories  values.TrainingTaxonomies
	TrainerID   values.TrainerID
	Name        values.TrainingName
	Description values.TrainingDescription
}
