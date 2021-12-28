package events

import valuesObjects "fitness-dimension/core/routines/routine/values-objects"

type RoutineCreated struct {
	ID          valuesObjects.RoutineID
	Name        valuesObjects.RoutineName
	UserID      valuesObjects.RoutineUserID
	TrainingsID []valuesObjects.RoutineTrainingID
	Description valuesObjects.RoutineDescription
}
