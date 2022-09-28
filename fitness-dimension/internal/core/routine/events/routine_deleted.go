package events

import "fitness-dimension/internal/core/routine/values"

type RoutineDeleted struct {
	ID values.RoutineID
}
