package commands

import (
	app "fitness-dimension/internal/app/routine"
	"fitness-dimension/internal/core/routine/values"

	"github.com/google/uuid"
)

type UpdateRoutine struct {
	app.RoutineCommand
	ID          uuid.UUID
	Name        string
	UserID      string
	TrainingsID []string
	Description string
}

func (c *UpdateRoutine) Execute(s *app.RoutineService) (interface{}, error) {
	var errs []error

	name, err := values.NewRoutineName(c.Name)
	errs = append(errs, err)

	userId, err := values.NewRoutineUserID(c.UserID)
	errs = append(errs, err)

	description, err := values.NewRoutineDescription(c.Description)
	errs = append(errs, err)

	var trainingsId []uuid.UUID
	for _, tId := range c.TrainingsID {
		id, err := uuid.Parse(tId)
		if err != nil {
			return nil, err
		}
		trainingsId = append(trainingsId, id)
	}

	trainings, err := values.NewRoutineTrainingIDs(trainingsId)
	errs = append(errs, err)

	r, err := s.Repository.Find(c.ID)
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

	return nil, nil
}
