package application

type SummaryPublisher interface {
	Publish(event interface{})
}
