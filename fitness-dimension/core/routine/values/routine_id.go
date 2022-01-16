package valueObjects

import "github.com/google/uuid"

type RoutineID struct {
	value uuid.UUID
}

func NewRoutineID(value uuid.UUID) (RoutineID, error) {
	return RoutineID{value: value}, nil
}

func (r *RoutineID) Value() uuid.UUID {
	return r.value
}
