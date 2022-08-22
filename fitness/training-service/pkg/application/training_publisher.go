package application

type TrainingPublisher interface {
	Publish(interface{}) error
}
