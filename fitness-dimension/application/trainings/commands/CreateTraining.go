package commands

import "github.com/google/uuid"

type CreateTraining struct {
	ID          uuid.UUID
	Categories  []string
	TrainerID   string
	Name        string
	Description string
}
