package models

type TrainingVideo struct {
	tableName  struct{} `pg:"videos,alias:v"`
	ID         string   `pg:"id,pk"`
	Name       string   `pg:"name"`
	Ext        string   `pg:"ext"`
	Buff       []byte   `pg:"buff"`
	TrainingID string   `pg:"training_id"`
}
type TrainingVideoMetadata struct {
	tableName  struct{} `pg:"videos,alias:v"`
	ID         string   `pg:"id,pk"`
	Name       string   `pg:"name"`
	Ext        string   `pg:"ext"`
	Length     int      `pg:"length_video"`
	TrainingID string   `pg:"training_id"`
}
