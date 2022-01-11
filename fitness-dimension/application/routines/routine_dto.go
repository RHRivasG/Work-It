package routines

type RoutineDto struct {
	ID          string   `json:"id"`
	Name        string   `json:"name"`
	Description string   `json:"description"`
	UserID      string   `json:"user_id"`
	TrainingsID []string `json:"trainings"`
}
