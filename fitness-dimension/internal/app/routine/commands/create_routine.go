package commands

import (
	app "fitness-dimension/internal/app/routine"
	"fitness-dimension/internal/core/routine"
	"fitness-dimension/internal/core/routine/values"

	"github.com/google/uuid"
)

type CreateRoutine struct {
	app.RoutineCommand
	Name        string
	UserID      string
	TrainingsID []string
	Description string
}

func (c *CreateRoutine) Execute(s *app.RoutineService) (interface{}, error) {

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

	return nil, nil
}
