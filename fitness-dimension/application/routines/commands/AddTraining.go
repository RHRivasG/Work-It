package commands

import "github.com/google/uuid"

type AddRoutineTraining struct {
	ID         uuid.UUID
	TrainingID uuid.UUID
}
