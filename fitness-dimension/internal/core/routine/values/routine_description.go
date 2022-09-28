package values

type RoutineDescription struct {
	value string
}

func NewRoutineDescription(value string) (RoutineDescription, error) {
	return RoutineDescription{value: value}, nil
}

func (r *RoutineDescription) Value() string {
	return r.value
}
