package models

type Routine struct {
	tableName   struct{} `pg:"routines,alias:r"`
	ID          string   `json:"id" pg:"id,pk"`
	Name        string   `json:"name" pg:"name"`
	UserID      string   `json:"userId" pg:"user_id"`
	Description string   `json:"description" pg:"description"`
	Trainings   []string `json:"trainings" pg:"-"`
}

type RoutineTraining struct {
	tableName  struct{} `pg:"routine_training,alias:rt"`
	RoutineID  string   `pg:"id_routine,pk"`
	TrainingID string   `pg:"id_training,pk"`
	Order      int      `pg:"order"`
}
