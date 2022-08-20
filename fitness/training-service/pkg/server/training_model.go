package server

type Training struct {
	tableName   struct{}       `pg:"trainings,alias:t"`
	ID          string         `pg:"id,pk"`
	Categories  []string       `pg:"categories,array"`
	TrainerID   string         `pg:"trainer_id"`
	Name        string         `pg:"name"`
	Description string         `pg:"description"`
	Video       *TrainingVideo `pg:"-"`
}

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
