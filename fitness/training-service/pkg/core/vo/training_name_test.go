package vo_test

import (
	"testing"
	"training-service/pkg/core/vo"
)

func TestNewTrainingName(t *testing.T) {
	newName := "TrainingName 1"
	name, err := vo.NewTrainingName(newName)

	if err != nil {
		t.Errorf("got %q, wanted %q", err, newName)
	}

	if name.Value != newName {
		t.Errorf("got %q, wanted %q", name.Value, newName)
	}

}

func TestTrainingNameCannotBeEmpty(t *testing.T) {
	var errs []error
	_, err := vo.NewTrainingName("")
	errs = append(errs, err)
	_, err = vo.NewTrainingName(" ")
	errs = append(errs, err)
	_, err = vo.NewTrainingName("  ")
	errs = append(errs, err)

	for _, err := range errs {
		if err != nil {

		}
	}

}
