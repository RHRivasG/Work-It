package models

type TrainingVideo struct {
	tableName  struct{} `pg:"videos,alias:t"`
	ID         string   `json:"id" pg:"id,pk"`
	Name       string   `json:"name" pg:"name"`
	Ext        string   `json:"ext" pg:"ext"`
	Buff       []byte   `json:"buff" pg:"buff"`
	TrainingID string   `json:"-" pg:"training_id"`
}
