package events

import valuesObjects "fitness-dimension/routines/routine/values-objects"

type RoutineCreated struct {
	ID          valuesObjects.RoutineID
	name        valuesObjects.RoutineName
	userID      valuesObjects.RoutineUserID
	trainingsID []valuesObjects.RoutineTrainingID
	description valuesObjects.RoutineDescription
}
