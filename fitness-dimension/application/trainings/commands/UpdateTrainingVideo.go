package commands

import "github.com/google/uuid"

type UpdateTrainingVideo struct {
	TrainingID uuid.UUID
	ID         uuid.UUID
	Name       string
	Ext        string
	Video      []byte
}
