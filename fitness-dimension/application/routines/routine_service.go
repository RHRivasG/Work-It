package routines

import (
	"fitness-dimension/application/routines/commands"

	"fitness-dimension/core/routine"
	valueObjects "fitness-dimension/core/routine/values"

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

		var errs []error

		name, err := valueObjects.NewRoutineName(command.Name)
		errs = append(errs, err)

		userId, err := valueObjects.NewRoutineUserID(command.UserID)
		errs = append(errs, err)

		description, err := valueObjects.NewRoutineDescription(command.Description)
		errs = append(errs, err)

		var trainingsId []uuid.UUID
		for _, tId := range command.TrainingsID {

			id, err := uuid.Parse(tId)
			if err != nil {
				return nil, err
			}

			trainingsId = append(trainingsId, id)
		}

		trainings, err := valueObjects.NewRoutineTrainingIDs(trainingsId)
		errs = append(errs, err)

		r, err := routine.CreateRoutine(name, userId, trainings, description)
		errs = append(errs, err)

		for _, err := range errs {
			if err != nil {
				return nil, err
			}
		}

		for _, i := range r.GetEvents() {
			s.Publisher.Publish(i)
		}

	case commands.UpdateRoutine:
		command := c.(commands.UpdateRoutine)

		var errs []error

		name, err := valueObjects.NewRoutineName(command.Name)
		errs = append(errs, err)

		userId, err := valueObjects.NewRoutineUserID(command.UserID)
		errs = append(errs, err)

		description, err := valueObjects.NewRoutineDescription(command.Description)
		errs = append(errs, err)

		var trainingsId []uuid.UUID
		for _, tId := range command.TrainingsID {
			id, err := uuid.Parse(tId)
			if err != nil {
				return nil, err
			}
			trainingsId = append(trainingsId, id)
		}

		trainings, err := valueObjects.NewRoutineTrainingIDs(trainingsId)
		errs = append(errs, err)

		r, err := s.Repository.Find(command.ID)
		errs = append(errs, err)

		for _, err := range errs {
			if err != nil {
				return nil, err
			}
		}

		r.Update(name, userId, trainings, description)
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
		trainingID, err := valueObjects.NewRoutineTrainingID(command.TrainingID)
		if err != nil {
			return nil, err
		}

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
		trainingID, err := valueObjects.NewRoutineTrainingID(command.TrainingID)
		if err != nil {
			return nil, err
		}

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
