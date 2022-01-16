package values

type RoutineTrainingOrder struct {
	value int
}

func NewRoutineTrainingOrder(value int) (RoutineTrainingOrder, error) {
	return RoutineTrainingOrder{value: value}, nil
}

func (r *RoutineTrainingOrder) Value() int {
	return r.value
}
