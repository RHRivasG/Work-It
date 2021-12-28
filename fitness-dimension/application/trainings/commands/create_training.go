package commands

type CreateTraining struct {
	Categories  []string `json:"categories"`
	TrainerID   string   `json:"trainerID"`
	Name        string   `json:"name"`
	Description string   `json:"description"`
}
