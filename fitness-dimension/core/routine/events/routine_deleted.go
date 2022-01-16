package events

import valueObjects "fitness-dimension/core/routine/values"

type RoutineDeleted struct {
	ID valueObjects.RoutineID
}
