package core

import (
	"summary-service/internal/core/events"
	"summary-service/internal/core/vo"

	"github.com/google/uuid"
)

type Summary struct {
	ID            *vo.SummaryID
	Routine       *vo.SummaryRoutine
	MinTime       *vo.SummaryMinTime
	MaxTime       *vo.SummaryMaxTime
	eventRecorder []interface{}
}

func (s *Summary) Events() []interface{} {
	return s.eventRecorder
}

func (s *Summary) addEvent(event interface{}) {
	s.eventRecorder = append(s.eventRecorder, event)
}

func CreateSummary(routine *vo.SummaryRoutine) (*Summary, error) {
	id, err := vo.NewSummaryID(uuid.New())
	if err != nil {
		return nil, err
	}

	summary := &Summary{
		ID:      id,
		Routine: routine,
	}

	summary.addEvent(events.SummaryCreated{
		ID:      summary.ID,
		Routine: summary.Routine,
	})

	return summary, nil
}

func (s *Summary) Update(t vo.SummaryTime) error {

	var err error
	updated := false

	if (s.MinTime == nil) || (s.MinTime.Value().Microseconds() == 0) || (s.MinTime != nil && s.MinTime.Value() > t.Value()) {
		s.MinTime, err = vo.NewSummaryMinTime(t.Value())
		if err != nil {
			return err
		}
		updated = true
	}

	if (s.MaxTime == nil) || (s.MaxTime.Value().Microseconds() == 0) || (s.MaxTime != nil && s.MaxTime.Value() < t.Value()) {
		s.MaxTime, err = vo.NewSummaryMaxTime(t.Value())
		if err != nil {
			return err
		}
		updated = true
	}

	if updated {
		s.addEvent(events.SummaryUpdated{
			ID:      s.ID,
			Routine: s.Routine,
			MinTime: s.MinTime,
			MaxTime: s.MaxTime,
		})
	}

	return nil
}
