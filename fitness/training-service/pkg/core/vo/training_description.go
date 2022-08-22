package vo

type TrainingDescription struct {
	value string
}

func NewTrainingDescription(value string) (*TrainingDescription, error) {
	return &TrainingDescription{value: value}, nil
}

func (t *TrainingDescription) Value() string {
	return t.value
}
