package events

import valueObjects "fitness-dimension/core/routine/values"

type TrainingAdded struct {
	ID         valueObjects.RoutineID
	TrainingID valueObjects.RoutineTrainingID
	Order      valueObjects.RoutineTrainingOrder
}
