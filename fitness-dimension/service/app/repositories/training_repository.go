package repositories

import (
	"fitness-dimension/application/trainings"
	"fitness-dimension/core/trainings/training"
	"fitness-dimension/core/trainings/training/entities"
	valuesObjects "fitness-dimension/core/trainings/training/values-objects"
	"fitness-dimension/service/app/models"
	"log"

	"github.com/go-pg/pg/v10"
	"github.com/google/uuid"
)

type PgTrainingRepository struct {
	trainings.TrainingRepository
	DB *pg.DB
}

func (r PgTrainingRepository) Get(id string) (*training.Training, error) {
	var trainingModel models.Training
	err := r.DB.Model().Table("trainings").
		Where("id = ?", id).
		Select(&trainingModel)

	if err != nil && err != pg.ErrNoRows {
		return nil, err
	}

	if err == pg.ErrNoRows {
		return nil, nil
	}

	trainingId, err := uuid.Parse(trainingModel.ID)
	if err != nil {
		return nil, err
	}

	return &training.Training{
		ID:          valuesObjects.TrainingID{Value: trainingId},
		Name:        valuesObjects.TrainingName{Value: trainingModel.Name},
		Description: valuesObjects.TrainingDescription{Value: trainingModel.Description},
		TrainerID:   valuesObjects.TrainerID{Value: trainingModel.TrainerID},
		Categories:  valuesObjects.TrainingTaxonomies{Values: trainingModel.Categories},
		Video:       nil,
	}, nil
}

func (r PgTrainingRepository) GetByTrainer(id string) ([]training.Training, error) {
	var trainingList []models.Training
	err := r.DB.Model().Table("trainings").Where("trainer_id = ?", id).Select(&trainingList)

	if err != nil && err != pg.ErrNoRows {
		return nil, err
	}

	if err == pg.ErrNoRows {
		return nil, nil
	}

	var trainings []training.Training
	for _, trainingItem := range trainingList {

		trainingId, err := uuid.Parse(trainingItem.ID)
		if err != nil {
			return nil, err
		}

		trainings = append(trainings, training.Training{
			ID:          valuesObjects.TrainingID{Value: trainingId},
			TrainerID:   valuesObjects.TrainerID{Value: trainingItem.TrainerID},
			Name:        valuesObjects.TrainingName{Value: trainingItem.Name},
			Description: valuesObjects.TrainingDescription{Value: trainingItem.Description},
			Categories:  valuesObjects.TrainingTaxonomies{Values: trainingItem.Categories},
			Video:       nil,
		})
	}

	return trainings, nil

}

func (r PgTrainingRepository) GetAll() ([]training.Training, error) {

	var trainingList []models.Training
	err := r.DB.Model().Table("trainings").Select(&trainingList)

	if err != nil && err != pg.ErrNoRows {
		return nil, err
	}

	if err == pg.ErrNoRows {
		return nil, nil
	}

	var trainings []training.Training
	for _, trainingItem := range trainingList {
		id, err := uuid.Parse(trainingItem.ID)
		if err != nil {
			return nil, err
		}

		trainings = append(trainings, training.Training{
			ID:          valuesObjects.TrainingID{Value: id},
			TrainerID:   valuesObjects.TrainerID{Value: trainingItem.TrainerID},
			Name:        valuesObjects.TrainingName{Value: trainingItem.Name},
			Description: valuesObjects.TrainingDescription{Value: trainingItem.Description},
			Categories:  valuesObjects.TrainingTaxonomies{Values: trainingItem.Categories},
			Video:       nil,
		})
	}

	return trainings, nil
}

func (r PgTrainingRepository) GetVideo(id string) *entities.TrainingVideo {
	var video models.TrainingVideo
	err := r.DB.Model().Table("videos").Where("training_id = ?", id).Select(&video)
	if err != nil && err != pg.ErrNoRows {
		log.Fatal(err)
	} else if err == pg.ErrNoRows {
		return nil
	}

	videoID, err := uuid.Parse(video.ID)
	if err != nil {
		log.Fatal(err)
	}

	return &entities.TrainingVideo{
		ID:   valuesObjects.TrainingVideoID{Value: videoID},
		Name: valuesObjects.TrainingVideoName{Value: video.Name},
		Ext:  valuesObjects.TrainingVideoExt{Value: video.Ext},
		Buff: valuesObjects.TrainingVideoBuffer{Value: video.Buff},
	}
}
