package commands

import (
	app "fitness-dimension/internal/app/routine"

	"github.com/google/uuid"
)

type DeleteRoutine struct {
	app.RoutineCommand
	ID uuid.UUID
}

func (c *DeleteRoutine) Execute(s *app.RoutineService) (interface{}, error) {
	r, err := s.Repository.Find(c.ID)
	if err != nil {
		return nil, err
	}

	r.Destroy()
	for _, i := range r.GetEvents() {
		s.Publisher.Publish(i)
	}

	return nil, nil
}
