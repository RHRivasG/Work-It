package models

type Routine struct {
	tableName   struct{} `pg:"routines,alias:r"`
	ID          string   `pg:"id,pk"`
	Name        string   `pg:"name"`
	UserID      string   `pg:"user_id"`
	Description string   `pg:"description"`
}

type RoutineTraining struct {
	tableName  struct{} `pg:"routine_training,alias:rt"`
	RoutineID  string   `pg:"id_routine,pk"`
	TrainingID string   `pg:"id_training,pk"`
	Order      int      `pg:"order"`
}
