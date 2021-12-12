package commands

import "github.com/google/uuid"

type UpdateTrainingVideo struct {
	ID    uuid.UUID
	Name  string
	Ext   string
	Video []byte
}
