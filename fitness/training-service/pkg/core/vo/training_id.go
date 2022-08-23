package vo

import (
	"errors"

	"github.com/google/uuid"
)

type TrainingID struct {
	Value uuid.UUID
}

func NewTrainingID(value uuid.UUID) (*TrainingID, error) {
	if value == uuid.Nil {
		return nil, errors.New("The ID cannot be empty")
	}
	return &TrainingID{Value: value}, nil
}

/*
func (t *TrainingID) Value() uuid.UUID {
	return t.value
}
*/
