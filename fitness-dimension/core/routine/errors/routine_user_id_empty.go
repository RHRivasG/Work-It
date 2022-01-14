package errors

type RoutineUserIdEmpty struct{}

func (r *RoutineUserIdEmpty) Error() string {
	return "The userID shouldn't be empty"
}
