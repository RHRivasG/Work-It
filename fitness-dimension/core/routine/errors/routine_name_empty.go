package errors

type RoutineNameEmpty struct{}

func (r *RoutineNameEmpty) Error() string {
	return "The name shouldn't be empty"
}
