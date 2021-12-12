package events

import valuesObjects "fitness-dimension-core/routines/routine/values-objects"

type TrainingRemoved struct {
	ID         valuesObjects.RoutineID
	TrainingID valuesObjects.RoutineTrainingID
}
