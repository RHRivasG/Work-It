package events

import "fitness-dimension/internal/core/routine/values"

type RoutineUpdated struct {
	ID          values.RoutineID
	Name        values.RoutineName
	UserID      values.RoutineUserID
	TrainingsID values.RoutineTrainingIDs
	Description values.RoutineDescription
}
