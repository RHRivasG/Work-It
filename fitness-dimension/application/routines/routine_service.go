package routines

import (
	"fitness-dimension/application/routines/commands"
	"fitness-dimension/application/routines/repositories"

	"fitness-dimension/core/routines/routine"
	valuesObjects "fitness-dimension/core/routines/routine/values-objects"
)

type RoutineService struct {
	Repository repositories.RoutineRepository
	Publisher  RoutinePublisher
}

func (s *RoutineService) Handle(c interface{}) {
	switch c.(type) {
	case commands.CreateRoutine:
		command := c.(commands.CreateRoutine)

		name := valuesObjects.RoutineName{Value: command.Name}
		userId := valuesObjects.RoutineUserID{Value: command.UserID}
		description := valuesObjects.RoutineDescription{Value: command.Description}
		trainingsId := []valuesObjects.RoutineTrainingID{}

		r := routine.CreateRoutine(name, userId, trainingsId, description)
		for _, i := range r.GetEvents() {
			s.Publisher.Publish(i)
		}

	case commands.UpdateRoutine:
		command := c.(commands.UpdateRoutine)

		name := valuesObjects.RoutineName{Value: command.Name}
		userId := valuesObjects.RoutineUserID{Value: command.UserID}
		description := valuesObjects.RoutineDescription{Value: command.Description}
		trainings := []valuesObjects.RoutineTrainingID{}

		r := s.Repository.Find(command.ID)
		r.Update(name, userId, trainings, description)

	case commands.DeleteRoutine:
		command := c.(commands.DeleteRoutine)

		r := s.Repository.Find(command.ID)
		r.Destroy()

	case commands.AddRoutineTraining:
		command := c.(commands.AddRoutineTraining)
		trainingID := valuesObjects.RoutineTrainingID{Value: command.TrainingID}

		r := s.Repository.Find(command.ID)
		r.AddTraining(trainingID)

	case commands.RemoveRoutineTraining:
		command := c.(commands.RemoveRoutineTraining)
		trainingID := valuesObjects.RoutineTrainingID{Value: command.TrainingID}

		r := s.Repository.Find(command.ID)
		r.RemoveTraining(trainingID)
	}
}
