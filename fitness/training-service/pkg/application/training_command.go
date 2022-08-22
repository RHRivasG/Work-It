package application

type TrainingCommand interface {
	Execute(TrainingService) (interface{}, error)
}
