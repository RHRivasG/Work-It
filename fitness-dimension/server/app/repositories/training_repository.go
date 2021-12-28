package repositories

import (
	"fitness-dimension/application/trainings/repositories"
	"fitness-dimension/core/trainings/training"

	"github.com/go-pg/pg/v10"
	"github.com/google/uuid"
)

type PgTrainingRepository struct {
	repositories.TrainingRepository
	DB *pg.DB
}

func (r PgTrainingRepository) Find(id uuid.UUID) training.Training {
	/*
		var training models.Training
		err := r.DB.Model().Table("trainings").
			Where("id = ?", id).
			Select(&training)
		return training, err
	*/
	return training.Training{}
}

func (r PgTrainingRepository) GetAll() []training.Training {
	/*
		var trainings []models.Training
		err := r.DB.Model().Table("trainings").Select(&trainings)
		return trainings, err
	*/
	return nil
}
