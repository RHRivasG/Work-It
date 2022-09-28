package events

import "fitness-dimension/internal/core/routine/values"

type TrainingRemoved struct {
	ID         values.RoutineID
	TrainingID values.RoutineTrainingID
}
