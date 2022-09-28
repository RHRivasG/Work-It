package vo_test

import (
	"errors"
	"testing"
	"training-service/pkg/core/vo"

	"github.com/google/uuid"
)

func TestNewTrainingId(t *testing.T) {
	newId := uuid.New()
	id, err := vo.NewTrainingID(newId)

	if err != nil {
		t.Errorf("got %q, wanted %q", err, newId)
	}

	if id.Value != newId {
		t.Errorf("got %q, wanted %q", id.Value, newId)
	}
}

func TestTrainingIdCannotBeEmpty(t *testing.T) {
	_, err := vo.NewTrainingID(uuid.Nil)
	if err == nil {
		t.Errorf("got a nil error, wanted %q", errors.New("The ID cannot be empty"))
	}
}
