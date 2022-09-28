package vo

type TrainingDescription struct {
	Value string
}

func NewTrainingDescription(value string) (*TrainingDescription, error) {
	return &TrainingDescription{Value: value}, nil
}
