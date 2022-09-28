package app

import (
	"fitness-dimension/internal/core/routine"

	"github.com/google/uuid"
)

type RoutineRepository interface {
	Find(uuid.UUID) (*routine.Routine, error)
	GetAll(userId string) ([]routine.Routine, error)
}
