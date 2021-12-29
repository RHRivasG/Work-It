package repositories

import (
	"fitness-dimension/application/routines/repositories"
	"fitness-dimension/core/routines/routine"
	valuesObjects "fitness-dimension/core/routines/routine/values-objects"
	"fitness-dimension/server/app/models"
	"log"

	"github.com/go-pg/pg/v10"
	"github.com/google/uuid"
)

type PgRoutineRepository struct {
	repositories.RoutineRepository
	DB *pg.DB
}

func (r PgRoutineRepository) Find(id uuid.UUID) routine.Routine {
	var routineModel models.Routine
	err := r.DB.Model().Table("routines").
		Where("id = ?", id.String()).
		Select(&routineModel)
	if err != nil {
		log.Fatal(err)
	}

	id, err = uuid.Parse(routineModel.ID)
	if err != nil {
		log.Fatal(err)
	}

	return routine.Routine{
		ID:          valuesObjects.RoutineID{Value: id},
		Name:        valuesObjects.RoutineName{Value: routineModel.Name},
		UserID:      valuesObjects.RoutineUserID{Value: routineModel.UserID},
		Description: valuesObjects.RoutineDescription{Value: routineModel.Description},
	}

}

func (r PgRoutineRepository) GetAll() []routine.Routine {

	var routineList []models.Routine
	err := r.DB.Model().Table("routines").Select(&routineList)
	if err != nil {
		log.Fatal(err)
	}

	var routines []routine.Routine

	for _, routineItem := range routineList {
		id, err := uuid.Parse(routineItem.ID)
		if err != nil {
			log.Fatal(err)
		}
		routines = append(routines, routine.Routine{
			ID:          valuesObjects.RoutineID{Value: id},
			Name:        valuesObjects.RoutineName{Value: routineItem.Name},
			UserID:      valuesObjects.RoutineUserID{Value: routineItem.UserID},
			Description: valuesObjects.RoutineDescription{Value: routineItem.Description},
		})
	}

	return routines
}
