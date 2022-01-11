package repositories

import (
	"fitness-dimension/application/routines"
	"fitness-dimension/core/routines/routine"
	valuesObjects "fitness-dimension/core/routines/routine/values-objects"
	"fitness-dimension/service/app/models"

	"github.com/go-pg/pg/v10"
	"github.com/google/uuid"
)

type PgRoutineRepository struct {
	routines.RoutineRepository
	DB *pg.DB
}

func (r PgRoutineRepository) Find(id uuid.UUID) (*routine.Routine, error) {
	var routineModel models.Routine
	err := r.DB.Model().Table("routines").
		Where("id = ?", id.String()).
		Select(&routineModel)

	if err != nil && err != pg.ErrNoRows {
		return nil, err
	}

	if err == pg.ErrNoRows {
		return nil, nil
	}

	id, err = uuid.Parse(routineModel.ID)
	if err != nil {
		return nil, err
	}

	trainingsId, err := r.getTrainings(routineModel)
	if err != nil {
		return nil, err
	}

	return &routine.Routine{
		ID:          valuesObjects.RoutineID{Value: id},
		Name:        valuesObjects.RoutineName{Value: routineModel.Name},
		UserID:      valuesObjects.RoutineUserID{Value: routineModel.UserID},
		Description: valuesObjects.RoutineDescription{Value: routineModel.Description},
		TrainingsID: valuesObjects.RoutineTrainingIDs{Values: trainingsId},
	}, nil

}

func (r PgRoutineRepository) GetAll(userId string) ([]routine.Routine, error) {

	var routineList []models.Routine
	err := r.DB.Model().Table("routines").Where("user_id = ?", userId).Select(&routineList)

	if err != nil && err != pg.ErrNoRows {
		return nil, err
	}

	if err == pg.ErrNoRows {
		return nil, nil
	}

	var routines []routine.Routine
	for _, routineItem := range routineList {

		trainingsId, err := r.getTrainings(routineItem)
		if err != nil {
			return nil, err
		}

		id, err := uuid.Parse(routineItem.ID)
		if err != nil {
			return nil, err
		}

		routines = append(routines, routine.Routine{
			ID:          valuesObjects.RoutineID{Value: id},
			Name:        valuesObjects.RoutineName{Value: routineItem.Name},
			UserID:      valuesObjects.RoutineUserID{Value: routineItem.UserID},
			Description: valuesObjects.RoutineDescription{Value: routineItem.Description},
			TrainingsID: valuesObjects.RoutineTrainingIDs{Values: trainingsId},
		})

	}

	return routines, nil
}

func (r PgRoutineRepository) getTrainings(routineItem models.Routine) ([]uuid.UUID, error) {
	var trainings []models.RoutineTraining
	err := r.DB.Model().Table("routine_training").
		Where("id_routine = ?", routineItem.ID).
		Order("order ASC").
		Select(&trainings)

	if err != nil && err != pg.ErrNoRows {
		return nil, err
	}

	if err == pg.ErrNoRows {
		return nil, nil
	}

	var trainingsId []uuid.UUID
	for _, tId := range trainings {
		i, err := uuid.Parse(tId.TrainingID)
		if err != nil {
			return nil, err
		}

		trainingsId = append(trainingsId, i)
	}

	return trainingsId, nil
}
