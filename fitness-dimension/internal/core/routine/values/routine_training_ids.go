package values

import "github.com/google/uuid"

type RoutineTrainingIDs struct {
	values []uuid.UUID
}

func NewRoutineTrainingIDs(values []uuid.UUID) (RoutineTrainingIDs, error) {
	return RoutineTrainingIDs{values: values}, nil
}

func (r *RoutineTrainingIDs) Values() []uuid.UUID {
	return r.values
}
