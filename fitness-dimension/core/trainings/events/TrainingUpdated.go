package events

import valuesObjects "fitness-dimension/core/trainings/training/values-objects"

type TrainingUpdated struct {
	ID          valuesObjects.TrainingID
	Categories  []valuesObjects.TrainingTaxonomy
	TrainerID   valuesObjects.TrainerID
	Name        valuesObjects.TrainingName
	Description valuesObjects.TrainingDescription
}
