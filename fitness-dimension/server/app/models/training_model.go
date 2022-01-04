package models

type Training struct {
	tableName   struct{} `pg:"trainings,alias:t"`
	ID          string   `pg:"id,pk"`
	TrainerID   string   `pg:"trainer_id"`
	Name        string   `pg:"name"`
	Description string   `pg:"description"`
}

type Taxonomy struct {
	ID   string `pg:"id,pk"`
	Name string `pg:"name"`
}
