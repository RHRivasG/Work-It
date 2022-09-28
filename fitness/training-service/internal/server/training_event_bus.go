package server

import (
	"training-service/pkg/application"

	"github.com/reactivex/rxgo/v2"
)

type TrainingEventBus struct {
	application.TrainingPublisher
	Observable rxgo.Observable
	ch         chan rxgo.Item
	errCh      chan error
	resCh      chan interface{}
}

func NewTrainingEventBus() *TrainingEventBus {
	ch := make(chan rxgo.Item)
	errCh := make(chan error)
	resCh := make(chan interface{})
	observable := rxgo.FromChannel(ch)

	return &TrainingEventBus{
		ch:         ch,
		resCh:      resCh,
		errCh:      errCh,
		Observable: observable,
	}
}

func (bus *TrainingEventBus) AddEventHandler(eventHandler TrainingEventHandler) {
	bus.Observable.DoOnNext(func(item interface{}) {
		err := eventHandler.Handle(item)
		if err != nil {
			bus.errCh <- err
		} else {
			bus.resCh <- nil
		}
	})
}

func (bus *TrainingEventBus) Publish(event interface{}) error {
	bus.ch <- rxgo.Of(event)
	select {
	case err := <-bus.errCh:
		return err
	case <-bus.resCh:
		return nil
	}
}
