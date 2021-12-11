package events

import valuesObjects "fitness-dimension/trainings/training/values-objects"

type TrainingCreated struct {
	ID          valuesObjects.TrainingID
	Categories  []valuesObjects.TrainingTaxonomy
	TrainerID   valuesObjects.TrainerID
	Name        valuesObjects.TrainingName
	Description valuesObjects.TrainingDescription
}
