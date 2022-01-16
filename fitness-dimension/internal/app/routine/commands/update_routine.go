package commands

import "github.com/google/uuid"

type UpdateRoutine struct {
	ID          uuid.UUID `json:"id"`
	Name        string    `json:"name"`
	UserID      string    `json:"userID"`
	TrainingsID []string  `json:"trainings"`
	Description string    `json:"description"`
}
