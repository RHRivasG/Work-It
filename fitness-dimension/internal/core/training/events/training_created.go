package events

import "fitness-dimension/internal/core/training/values"

type TrainingCreated struct {
	ID          values.TrainingID
	Categories  values.TrainingTaxonomies
	TrainerID   values.TrainerID
	Name        values.TrainingName
	Description values.TrainingDescription
}
