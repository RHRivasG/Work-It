package events

import "fitness-dimension/internal/core/routine/values"

type TrainingAdded struct {
	ID         values.RoutineID
	TrainingID values.RoutineTrainingID
	Order      values.RoutineTrainingOrder
}
