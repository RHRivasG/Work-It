package events

import valueObjects "fitness-dimension/core/routine/values"

type RoutineCreated struct {
	ID          valueObjects.RoutineID
	Name        valueObjects.RoutineName
	UserID      valueObjects.RoutineUserID
	TrainingsID valueObjects.RoutineTrainingIDs
	Description valueObjects.RoutineDescription
}
