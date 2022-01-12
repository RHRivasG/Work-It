package models

type TrainingVideo struct {
	tableName  struct{} `pg:"videos,alias:v"`
	ID         string   `pg:"id,pk"`
	Name       string   `pg:"name"`
	Ext        string   `pg:"ext"`
	Buff       []byte   `pg:"buff"`
	Length     int      `pg:"length_video"`
	TrainingID string   `pg:"training_id"`
}
