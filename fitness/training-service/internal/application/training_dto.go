package application

type TrainingDto struct {
	ID          string            `json:"id"`
	Name        string            `json:"name"`
	TrainerID   string            `json:"trainerId"`
	Description string            `json:"description"`
	Categories  []string          `json:"categories"`
	Video       *TrainingVideoDto `json:"video"`
}

type TrainingVideoDto struct {
	ID     string `json:"id"`
	Name   string `json:"name"`
	Ext    string `json:"ext"`
	Buff   []byte `json:"buff"`
	Length int    `json:"length"`
}
