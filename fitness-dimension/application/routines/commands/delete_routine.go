package commands

import "github.com/google/uuid"

type DeleteRoutine struct {
	ID uuid.UUID `json:"id"`
}
