package repositories

import (
	"fitness-dimension/application/routines/repositories"
	"fitness-dimension/core/routines/routine"

	"github.com/go-pg/pg/v10"
	"github.com/google/uuid"
)

type PgRoutineRepository struct {
	repositories.RoutineRepository
	DB *pg.DB
}

func (r PgRoutineRepository) Find(id uuid.UUID) routine.Routine {
	/*
		var routine models.Routine
		err := r.DB.Model().Table("routines").
			Where("id = ?", id).
			Select(&routine)
		return routine, err
	*/
	return routine.Routine{}

}

func (r PgRoutineRepository) GetAll() []routine.Routine {
	/*
		var routines []models.Routine
		err := r.DB.Model().Table("routines").Select(&routines)
		return routines, err
	*/

	return nil
}
