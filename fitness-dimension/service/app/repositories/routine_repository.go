package repositories

import (
	"fitness-dimension/application/routines/repositories"
	"fitness-dimension/core/routines/routine"
	valuesObjects "fitness-dimension/core/routines/routine/values-objects"
	"fitness-dimension/service/app/models"
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

	trainingsId := r.getTrainings(routineModel)

	return routine.Routine{
		ID:          valuesObjects.RoutineID{Value: id},
		Name:        valuesObjects.RoutineName{Value: routineModel.Name},
		UserID:      valuesObjects.RoutineUserID{Value: routineModel.UserID},
		Description: valuesObjects.RoutineDescription{Value: routineModel.Description},
		TrainingsID: valuesObjects.RoutineTrainingIDs{Values: trainingsId},
	}

}

func (r PgRoutineRepository) GetAll(userId string) []routine.Routine {

	var routineList []models.Routine
	err := r.DB.Model().Table("routines").Where("user_id = ?", userId).Select(&routineList)
	if err != nil {
		log.Fatal(err)
	}

	var routines []routine.Routine
	for _, routineItem := range routineList {

		trainingsId := r.getTrainings(routineItem)

		id, err := uuid.Parse(routineItem.ID)
		if err != nil {
			log.Fatal(err)
		}
		routines = append(routines, routine.Routine{
			ID:          valuesObjects.RoutineID{Value: id},
			Name:        valuesObjects.RoutineName{Value: routineItem.Name},
			UserID:      valuesObjects.RoutineUserID{Value: routineItem.UserID},
			Description: valuesObjects.RoutineDescription{Value: routineItem.Description},
			TrainingsID: valuesObjects.RoutineTrainingIDs{Values: trainingsId},
		})

	}

	return routines
}

func (r PgRoutineRepository) getTrainings(routineItem models.Routine) []uuid.UUID {
	var trainings []models.RoutineTraining
	err := r.DB.Model().Table("routine_training").
		Where("id_routine = ?", routineItem.ID).
		Order("order ASC").
		Select(&trainings)

	if err != nil {
		if err == pg.ErrNoRows {
			trainings = nil
		} else {
			log.Fatal(err)
		}
	}

	var trainingsId []uuid.UUID
	for _, tId := range trainings {
		i, err := uuid.Parse(tId.TrainingID)
		if err != nil {
			log.Fatal(err)
		}

		trainingsId = append(trainingsId, i)
	}

	return trainingsId
}
