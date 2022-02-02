package vo

import "time"

type SummaryMinTime struct {
	value time.Duration
}

func NewSummaryMinTime(value time.Duration) (*SummaryMinTime, error) {
	return &SummaryMinTime{value: value}, nil
}

func (s *SummaryMinTime) Value() time.Duration {
	return s.value
}
