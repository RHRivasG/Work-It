package repositories

import (
	"fitness-dimension/core/routines/routine"

	"github.com/google/uuid"
)

type RoutineRepository interface {
	Find(uuid.UUID) routine.Routine
	Delete(uuid.UUID)
}