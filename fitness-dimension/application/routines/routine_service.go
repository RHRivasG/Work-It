package routines

import (
	"fitness-dimension/application/routines/commands"
	"fitness-dimension/application/routines/repositories"
	"log"

	"fitness-dimension/core/routines/routine"
	valuesObjects "fitness-dimension/core/routines/routine/values-objects"

	"github.com/google/uuid"
)

type RoutineService struct {
	Publisher  RoutinePublisher
	Repository repositories.RoutineRepository
}

func (s *RoutineService) Handle(c interface{}) {
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
				log.Fatal(err)
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
				log.Fatal(err)
			}
			trainingsId.Values = append(trainingsId.Values, id)
		}

		r := s.Repository.Find(command.ID)
		r.Update(name, userId, trainingsId, description)
		for _, i := range r.GetEvents() {
			s.Publisher.Publish(i)
		}

	case commands.DeleteRoutine:
		command := c.(commands.DeleteRoutine)

		r := s.Repository.Find(command.ID)
		r.Destroy()
		for _, i := range r.GetEvents() {
			s.Publisher.Publish(i)
		}

	case commands.AddRoutineTraining:
		command := c.(commands.AddRoutineTraining)
		trainingID := valuesObjects.RoutineTrainingID{Value: command.TrainingID}

		r := s.Repository.Find(command.ID)
		r.AddTraining(trainingID)
		for _, i := range r.GetEvents() {
			s.Publisher.Publish(i)
		}

	case commands.RemoveRoutineTraining:
		command := c.(commands.RemoveRoutineTraining)
		trainingID := valuesObjects.RoutineTrainingID{Value: command.TrainingID}

		r := s.Repository.Find(command.ID)
		r.RemoveTraining(trainingID)
		for _, i := range r.GetEvents() {
			s.Publisher.Publish(i)
		}
	}
}

func (s *RoutineService) Get(id string) routine.Routine {
	routineId, err := uuid.Parse(id)
	if err != nil {
		log.Fatal(err)
	}

	return s.Repository.Find(routineId)
}

func (s *RoutineService) GetAll() []routine.Routine {
	return s.Repository.GetAll()
}
