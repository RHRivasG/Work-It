package vo

import "time"

type SummaryTime struct {
	value time.Duration
}

func NewSummaryTime(value time.Duration) (SummaryTime, error) {
	return SummaryTime{value: value}, nil
}

func (s SummaryTime) Value() time.Duration {
	return s.value
}
