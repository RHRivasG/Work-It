package commands

type CreateRoutine struct {
	Name        string   `json:"name"`
	UserID      string   `json:"userID"`
	TrainingsID []string `json:"trainings"`
	Description string   `json:"description"`
}
