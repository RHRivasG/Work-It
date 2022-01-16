package app

type RoutinePublisher interface {
	Publish(interface{})
}
