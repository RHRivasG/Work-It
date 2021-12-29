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

	var trainingList []models.Training
	err := r.DB.Model().Table("trainings").Select(&trainingList)
	if err != nil {
		log.Fatal(err)
	}

	var trainings []training.Training
	for _, trainingItem := range trainingList {
		id, err := uuid.Parse(trainingItem.ID)
		if err != nil {
			log.Fatal(err)
		}
		trainings = append(trainings, training.Training{
			ID:          valuesObjects.TrainingID{Value: id},
			Name:        valuesObjects.TrainingName{Value: trainingItem.Name},
			TrainerID:   valuesObjects.TrainerID{Value: trainingItem.TrainerID},
			Description: valuesObjects.TrainingDescription{Value: trainingItem.Description},
		})
	}

	return trainings
}
