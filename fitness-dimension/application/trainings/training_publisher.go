package trainings

type TrainingPublisher interface {
	Publish(interface{}) error
}
