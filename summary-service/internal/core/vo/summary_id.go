package vo

import "github.com/google/uuid"

type SummaryID struct {
	value uuid.UUID
}

func NewSummaryID(value uuid.UUID) (*SummaryID, error) {
	return &SummaryID{value: value}, nil
}

func (s *SummaryID) Value() uuid.UUID {
	return s.value
}
