package vo

import (
	"errors"
	"strings"
)

type TrainingName struct {
	Value string
}

func NewTrainingName(value string) (*TrainingName, error) {

	if len(strings.TrimSpace(value)) == 0 {
		return nil, errors.New("The name cannot be empty")
	}
	return &TrainingName{Value: value}, nil
}

/*
func (t *TrainingName) Value() string {
	return t.value
}
*/
