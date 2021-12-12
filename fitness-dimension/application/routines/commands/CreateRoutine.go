package commands

import "github.com/google/uuid"

type CreateRoutine struct {
	ID          uuid.UUID
	Name        string
	UserID      string
	TrainingsID []string
	Description string
}
