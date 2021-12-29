package repositories

import (
	"fitness-dimension/application/trainings/repositories"
	"fitness-dimension/core/trainings/training"
	valuesObjects "fitness-dimension/core/trainings/training/values-objects"
	"fitness-dimension/server/app/models"
	"log"

	"github.com/go-pg/pg/v10"
	"github.com/google/uuid"
)

type PgTrainingRepository struct {
	repositories.TrainingRepository
	DB *pg.DB
}

func (r PgTrainingRepository) Find(id uuid.UUID) training.Training {
	var trainingModel models.Training
	err := r.DB.Model().Table("trainings").
		Where("id = ?", id.String()).
		Select(&trainingModel)
	if err != nil {
		log.Fatal(err)
	}

	id, err = uuid.Parse(trainingModel.ID)
	if err != nil {
		log.Fatal(err)
	}

	return training.Training{
		ID:          valuesObjects.TrainingID{Value: id},
		Name:        valuesObjects.TrainingName{Value: trainingModel.Name},
		TrainerID:   valuesObjects.TrainerID{Value: trainingModel.TrainerID},
		Description: valuesObjects.TrainingDescription{Value: trainingModel.Description},
	}
}

func (r PgTrainingRepository) GetAll() []training.Training {
	/*
		var trainings []models.Training
		err := r.DB.Model().Table("trainings").Select(&trainings)
		return trainings, err
	*/
	return nil
}
