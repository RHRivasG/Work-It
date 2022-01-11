package models

type Training struct {
	tableName   struct{}       `pg:"trainings,alias:t"`
	ID          string         `json:"id" pg:"id,pk"`
	Categories  []string       `json:"categories" pg:"categories,array"`
	TrainerID   string         `json:"trainerId" pg:"trainer_id"`
	Name        string         `json:"name" pg:"name"`
	Description string         `json:"description" pg:"description"`
	Video       *TrainingVideo `json:"video" pg:"-"`
}
