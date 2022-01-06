package models

type Training struct {
	tableName   struct{} `pg:"trainings,alias:t"`
	ID          string   `pg:"id,pk"`
	Categories  []string `pg:"categories,array"`
	TrainerID   string   `pg:"trainer_id"`
	Name        string   `pg:"name"`
	Description string   `pg:"description"`
}
