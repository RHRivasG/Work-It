package commands

import "github.com/google/uuid"

type CreateTrainingVideo struct {
	TrainingID uuid.UUID
	Name       string `json:"name"`
	Ext        string `json:"ext"`
	Video      []byte `json:"buff"`
}
