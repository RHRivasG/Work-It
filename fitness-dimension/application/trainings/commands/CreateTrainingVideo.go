package commands

import "github.com/google/uuid"

type CreateTrainingVideo struct {
	ID    uuid.UUID
	Name  string
	Ext   string
	Video []byte
}
