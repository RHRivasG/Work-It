package vo

import "github.com/google/uuid"

type SummaryRoutine struct {
	value uuid.UUID
}

func NewSummaryRoutine(value uuid.UUID) (*SummaryRoutine, error) {
	return &SummaryRoutine{value: value}, nil
}

func (s *SummaryRoutine) Value() uuid.UUID {
	return s.value
}
