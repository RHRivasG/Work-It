package routines

import (
	"fitness-dimension/core/routine"

	"github.com/google/uuid"
)

type RoutineRepository interface {
	Find(uuid.UUID) (*routine.Routine, error)
	GetAll(userId string) ([]routine.Routine, error)
}
