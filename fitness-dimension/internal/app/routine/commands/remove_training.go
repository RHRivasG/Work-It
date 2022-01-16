package commands

import "github.com/google/uuid"

type RemoveRoutineTraining struct {
	ID         uuid.UUID
	TrainingID uuid.UUID
}
