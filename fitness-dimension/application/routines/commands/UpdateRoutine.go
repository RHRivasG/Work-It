package commands

import "github.com/google/uuid"

type UpdateRoutine struct {
	ID          uuid.UUID
	Name        string
	UserID      string
	TrainingsID []string
	Description string
}
