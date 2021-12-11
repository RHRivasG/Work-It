package routine

import (
	valuesObjects "fitness-dimension/routines/routine/values-objects"
)

type Training struct {
	name        valuesObjects.RoutineName
	userID      valuesObjects.RoutineUserID
	trainingsID []valuesObjects.RoutineTrainingID
	description valuesObjects.RoutineDescription
}
