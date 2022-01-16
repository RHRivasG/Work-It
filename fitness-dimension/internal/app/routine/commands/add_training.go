package commands

import "github.com/google/uuid"

type AddRoutineTraining struct {
	ID         uuid.UUID `json:"id"`
	TrainingID uuid.UUID `json:"trainingID"`
}
