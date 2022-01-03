package commands

import "github.com/google/uuid"

type UpdateTraining struct {
	ID          uuid.UUID
	Categories  []string
	TrainerID   string
	Name        string
	Description string
}
