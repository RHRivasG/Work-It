package routines

import (
	"fitness-dimension/application/routines/commands"

	"fitness-dimension/core/routines/routine"
	valuesObjects "fitness-dimension/core/routines/routine/values-objects"

	"github.com/google/uuid"
)

type RoutineService struct {
	Publisher  RoutinePublisher
	Repository RoutineRepository
}

func (s *RoutineService) Handle(c interface{}) (interface{}, error) {
	switch c.(type) {
	case commands.CreateRoutine:
		command := c.(commands.CreateRoutine)

		name := valuesObjects.RoutineName{Value: command.Name}
		userId := valuesObjects.RoutineUserID{Value: command.UserID}
		description := valuesObjects.RoutineDescription{Value: command.Description}
		trainingsId := valuesObjects.RoutineTrainingIDs{}

		for _, tId := range command.TrainingsID {
			id, err := uuid.Parse(tId)
			if err != nil {
				return nil, err
			}
			trainingsId.Values = append(trainingsId.Values, id)
		}

		r := routine.CreateRoutine(name, userId, trainingsId, description)
		for _, i := range r.GetEvents() {
			s.Publisher.Publish(i)
		}

	case commands.UpdateRoutine:
		command := c.(commands.UpdateRoutine)

		name := valuesObjects.RoutineName{Value: command.Name}
		userId := valuesObjects.RoutineUserID{Value: command.UserID}
		description := valuesObjects.RoutineDescription{Value: command.Description}
		trainingsId := valuesObjects.RoutineTrainingIDs{}

		for _, tId := range command.TrainingsID {
			id, err := uuid.Parse(tId)
			if err != nil {
				return nil, err
			}
			trainingsId.Values = append(trainingsId.Values, id)
		}

		r, err := s.Repository.Find(command.ID)
		if err != nil {
			return nil, err
		}

		r.Update(name, userId, trainingsId, description)
		for _, i := range r.GetEvents() {
			s.Publisher.Publish(i)
		}

	case commands.DeleteRoutine:
		command := c.(commands.DeleteRoutine)

		r, err := s.Repository.Find(command.ID)
		if err != nil {
			return nil, err
		}

		r.Destroy()
		for _, i := range r.GetEvents() {
			s.Publisher.Publish(i)
		}

	case commands.AddRoutineTraining:
		command := c.(commands.AddRoutineTraining)
		trainingID := valuesObjects.RoutineTrainingID{Value: command.TrainingID}

		r, err := s.Repository.Find(command.ID)
		if err != nil {
			return nil, err
		}

		r.AddTraining(trainingID)
		for _, i := range r.GetEvents() {
			s.Publisher.Publish(i)
		}

	case commands.RemoveRoutineTraining:
		command := c.(commands.RemoveRoutineTraining)
		trainingID := valuesObjects.RoutineTrainingID{Value: command.TrainingID}

		r, err := s.Repository.Find(command.ID)
		if err != nil {
			return nil, err
		}

		r.RemoveTraining(trainingID)
		for _, i := range r.GetEvents() {
			s.Publisher.Publish(i)
		}
	}
	return nil, nil
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
