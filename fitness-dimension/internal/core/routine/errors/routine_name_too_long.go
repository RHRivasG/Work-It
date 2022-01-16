package errors

type RoutineNameTooLong struct{}

func (r *RoutineNameTooLong) Error() string {
	return "The name should have max 255 characters"
}
