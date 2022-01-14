package events

import valueObjects "fitness-dimension/core/routine/values"

type TrainingRemoved struct {
	ID         valueObjects.RoutineID
	TrainingID valueObjects.RoutineTrainingID
}
