package app

type RoutineCommand interface {
	Execute(RoutineService) (interface{}, error)
}
