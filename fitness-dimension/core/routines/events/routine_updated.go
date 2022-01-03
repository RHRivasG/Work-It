package events

import valuesObjects "fitness-dimension/core/routines/routine/values-objects"

type RoutineUpdated struct {
	ID          valuesObjects.RoutineID
	Name        valuesObjects.RoutineName
	UserID      valuesObjects.RoutineUserID
	TrainingsID valuesObjects.RoutineTrainingIDs
	Description valuesObjects.RoutineDescription
}
