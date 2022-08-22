package vo_test

import (
	"testing"
	"training-service/internal/core/vo"
)

func TestNewTrainingDescription(t *testing.T) {
	newDescription := "This is a test description for a training"
	description, err := vo.NewTrainingDescription(newDescription)

	if err != nil {
		t.Errorf("got %q, wanted %q", err, newDescription)
	}

	if description.Value() != newDescription {
		t.Errorf("got %q, wanted %q", description.Value(), newDescription)
	}

}
