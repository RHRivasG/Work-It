package models

type TrainingVideo struct {
	tableName  struct{} `pg:"videos,alias:t"`
	ID         string   `pg:"id,pk"`
	Name       string   `pg:"name"`
	Ext        string   `pg:"ext"`
	Buff       []byte   `pg:"buff"`
	TrainingID string   `pg:"training_id"`
}
