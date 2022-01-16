package routine

import (
	app "fitness-dimension/internal/app/routine"
	"fitness-dimension/internal/core/routine"
	"fitness-dimension/internal/core/routine/values"

	"github.com/go-pg/pg/v10"
	"github.com/google/uuid"
)

type PgRoutineRepository struct {
	app.RoutineRepository
	DB *pg.DB
}

func (r PgRoutineRepository) Find(id uuid.UUID) (*routine.Routine, error) {
	var routineModel Routine
	err := r.DB.Model().Table("routines").
		Where("id = ?", id.String()).
		Select(&routineModel)

	if err != nil && err != pg.ErrNoRows {
		return nil, err
	}

	if err == pg.ErrNoRows {
		return nil, nil
	}

	var errs []error

	id, err = uuid.Parse(routineModel.ID)
	errs = append(errs, err)

	trainings, err := r.getTrainings(routineModel)
	errs = append(errs, err)

	for _, e := range errs {
		if e != nil {
			return nil, e
		}
	}

	routineId, err := values.NewRoutineID(id)
	errs = append(errs, err)

	name, err := values.NewRoutineName(routineModel.Name)
	errs = append(errs, err)

	userId, err := values.NewRoutineUserID(routineModel.UserID)
	errs = append(errs, err)

	description, err := values.NewRoutineDescription(routineModel.Description)
	errs = append(errs, err)

	trainingsId, err := values.NewRoutineTrainingIDs(trainings)
	errs = append(errs, err)

	for _, e := range errs {
		if e != nil {
			return nil, e
		}
	}

	return &routine.Routine{
		ID:          routineId,
		Name:        name,
		UserID:      userId,
		Description: description,
		TrainingsID: trainingsId,
	}, nil

}

func (r PgRoutineRepository) GetAll(userId string) ([]routine.Routine, error) {

	var routineList []Routine
	err := r.DB.Model().Table("routines").Where("user_id = ?", userId).Select(&routineList)

	if err != nil && err != pg.ErrNoRows {
		return nil, err
	}

	if err == pg.ErrNoRows {
		return nil, nil
	}

	var routines []routine.Routine
	for _, routineItem := range routineList {

		trainings, err := r.getTrainings(routineItem)
		if err != nil {
			return nil, err
		}

		id, err := uuid.Parse(routineItem.ID)
		if err != nil {
			return nil, err
		}

		var errs []error

		routineId, err := values.NewRoutineID(id)
		errs = append(errs, err)

		name, err := values.NewRoutineName(routineItem.Name)
		errs = append(errs, err)

		userId, err := values.NewRoutineUserID(routineItem.UserID)
		errs = append(errs, err)

		description, err := values.NewRoutineDescription(routineItem.Description)
		errs = append(errs, err)

		trainingsId, err := values.NewRoutineTrainingIDs(trainings)
		errs = append(errs, err)

		for _, e := range errs {
			if e != nil {
				return nil, e
			}
		}

		routines = append(routines, routine.Routine{
			ID:          routineId,
			Name:        name,
			UserID:      userId,
			Description: description,
			TrainingsID: trainingsId,
		})

	}

	return routines, nil
}

func (r PgRoutineRepository) getTrainings(routineItem Routine) ([]uuid.UUID, error) {
	var trainings []RoutineTraining
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
