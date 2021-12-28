package commands

import "github.com/google/uuid"

type CreateTrainingVideo struct {
	TrainingID uuid.UUID `json:"trainingID"`
	Name       string    `json:"name"`
	Ext        string    `json:"ext"`
	Video      []byte    `json:"video"`
}
