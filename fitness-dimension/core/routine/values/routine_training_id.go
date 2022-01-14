package valueObjects

import "github.com/google/uuid"

type RoutineTrainingID struct {
	value uuid.UUID
}

func NewRoutineTrainingID(value uuid.UUID) (RoutineTrainingID, error) {
	return RoutineTrainingID{value: value}, nil
}

func (r *RoutineTrainingID) Value() uuid.UUID {
	return r.value
}
