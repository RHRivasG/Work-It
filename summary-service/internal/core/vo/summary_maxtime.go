package vo

import "time"

type SummaryMaxTime struct {
	value time.Duration
}

func NewSummaryMaxTime(value time.Duration) (*SummaryMaxTime, error) {
	return &SummaryMaxTime{value: value}, nil
}

func (s *SummaryMaxTime) Value() time.Duration {
	return s.value
}
