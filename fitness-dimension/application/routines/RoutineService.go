package routines

import (
	"fitness-dimension/application/routines/commands"
	"fitness-dimension/application/routines/repositories"

	"fitness-dimension/core/routines/routine"
	valuesObjects "fitness-dimension/core/routines/routine/values-objects"
)

type RoutineService struct {
	RoutineRepository repositories.RoutineRepository
}

func (s *RoutineService) handle(c interface{}) {
	switch c.(type) {
	case commands.CreateRoutine:
		command := c.(commands.CreateRoutine)

		routineId := valuesObjects.RoutineID{Value: command.ID}
		name := valuesObjects.RoutineName{Value: command.Name}
		userId := valuesObjects.RoutineUserID{Value: command.UserID}
		description := valuesObjects.RoutineDescription{Value: command.Description}
		trainingsId := []valuesObjects.RoutineTrainingID{}

		routine.CreateRoutine(routineId, name, userId, trainingsId, description)

	case commands.UpdateRoutine:
		command := c.(commands.UpdateRoutine)

		name := valuesObjects.RoutineName{Value: command.Name}
		userId := valuesObjects.RoutineUserID{Value: command.UserID}
		description := valuesObjects.RoutineDescription{Value: command.Description}
		trainings := []valuesObjects.RoutineTrainingID{}

		r := s.RoutineRepository.Find(command.ID)
		r.Update(name, userId, trainings, description)

	case commands.DeleteRoutine:
		command := c.(commands.DeleteRoutine)

		r := s.RoutineRepository.Find(command.ID)
		r.Destroy()
		s.RoutineRepository.Delete(command.ID)

	case commands.AddRoutineTraining:
		command := c.(commands.AddRoutineTraining)
		trainingID := valuesObjects.RoutineTrainingID{Value: command.TrainingID}

		r := s.RoutineRepository.Find(command.ID)
		r.AddTraining(trainingID)

	case commands.RemoveRoutineTraining:
		command := c.(commands.RemoveRoutineTraining)
		trainingID := valuesObjects.RoutineTrainingID{Value: command.TrainingID}

		r := s.RoutineRepository.Find(command.ID)
		r.RemoveTraining(trainingID)
	}
}
