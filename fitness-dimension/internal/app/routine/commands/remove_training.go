package commands

import "github.com/google/uuid"

type RemoveRoutineTraining struct {
	ID         uuid.UUID `json:"id"`
	TrainingID uuid.UUID `json:"trainingID"`
}
