package app

type TrainingPublisher interface {
	Publish(interface{}) error
}
