package events

import valuesObjects "fitness-dimension/routines/routine/values-objects"

type RoutineTrainingRemoved struct {
	ID                valuesObjects.RoutineID
	RoutineTrainingID valuesObjects.RoutineTrainingID
}
