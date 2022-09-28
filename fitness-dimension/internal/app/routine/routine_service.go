package app

import (
	"fitness-dimension/internal/core/routine"

	"github.com/google/uuid"
)

type RoutineService struct {
	Publisher  RoutinePublisher
	Repository RoutineRepository
}

func (s *RoutineService) Get(id string) (*routine.Routine, error) {
	routineId, err := uuid.Parse(id)
	if err != nil {
		return nil, err
	}

	return s.Repository.Find(routineId)
}

func (s *RoutineService) GetAll(userId string) ([]routine.Routine, error) {
	return s.Repository.GetAll(userId)
}
