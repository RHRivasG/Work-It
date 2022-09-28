package app

type TrainingCommand interface {
	Execute(TrainingService) (interface{}, error)
}
