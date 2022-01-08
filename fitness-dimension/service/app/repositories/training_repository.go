package repositories

import (
	"fitness-dimension/application/trainings/repositories"
	"fitness-dimension/core/trainings/training"
	"fitness-dimension/core/trainings/training/entities"
	valuesObjects "fitness-dimension/core/trainings/training/values-objects"
	"fitness-dimension/service/app/models"
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

	var video models.TrainingVideo
	err = r.DB.Model().Table("videos").Where("training_id = ?", trainingModel.ID).Select(&video)

	if err != nil {
		if err == pg.ErrNoRows {
			return training.Training{
				ID:         valuesObjects.TrainingID{Value: id},
				Name:       valuesObjects.TrainingName{Value: trainingModel.Name},
				TrainerID:  valuesObjects.TrainerID{Value: trainingModel.TrainerID},
				Categories: valuesObjects.TrainingTaxonomies{Values: trainingModel.Categories},
				Video:      nil,
			}
		} else {
			log.Fatal(err)
		}
	}

	videoID, err := uuid.Parse(video.ID)
	if err != nil {
		log.Fatal(err)
	}

	return training.Training{
		ID:         valuesObjects.TrainingID{Value: id},
		Name:       valuesObjects.TrainingName{Value: trainingModel.Name},
		TrainerID:  valuesObjects.TrainerID{Value: trainingModel.TrainerID},
		Categories: valuesObjects.TrainingTaxonomies{Values: trainingModel.Categories},
		Video: &entities.TrainingVideo{
			ID:   valuesObjects.TrainingVideoID{Value: videoID},
			Name: valuesObjects.TrainingVideoName{Value: video.Name},
			Ext:  valuesObjects.TrainingVideoExt{Value: video.Ext},
			Buff: valuesObjects.TrainingVideoBuffer{Value: video.Buff},
		},
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

		var video models.TrainingVideo
		err := r.DB.Model().Table("videos").Where("training_id = ?", trainingItem.ID).Select(&video)

		trainingVideo := entities.TrainingVideo{}
		if err == nil {

			videoID, err := uuid.Parse(video.ID)
			if err != nil {
				log.Fatal(err)
			}

			trainingVideo.ID = valuesObjects.TrainingVideoID{Value: videoID}
			trainingVideo.Name = valuesObjects.TrainingVideoName{Value: video.Name}
			trainingVideo.Ext = valuesObjects.TrainingVideoExt{Value: video.Ext}
			trainingVideo.Buff = valuesObjects.TrainingVideoBuffer{Value: video.Buff}

			id, err := uuid.Parse(trainingItem.ID)
			if err != nil {
				log.Fatal(err)
			}

			trainings = append(trainings, training.Training{
				ID:          valuesObjects.TrainingID{Value: id},
				TrainerID:   valuesObjects.TrainerID{Value: trainingItem.TrainerID},
				Name:        valuesObjects.TrainingName{Value: trainingItem.Name},
				Description: valuesObjects.TrainingDescription{Value: trainingItem.Description},
				Categories:  valuesObjects.TrainingTaxonomies{Values: trainingItem.Categories},
				Video:       &trainingVideo,
			})

		} else if err == pg.ErrNoRows {

			id, err := uuid.Parse(trainingItem.ID)
			if err != nil {
				log.Fatal(err)
			}

			trainings = append(trainings, training.Training{
				ID:          valuesObjects.TrainingID{Value: id},
				TrainerID:   valuesObjects.TrainerID{Value: trainingItem.TrainerID},
				Name:        valuesObjects.TrainingName{Value: trainingItem.Name},
				Description: valuesObjects.TrainingDescription{Value: trainingItem.Description},
				Categories:  valuesObjects.TrainingTaxonomies{Values: trainingItem.Categories},
				Video:       nil,
			})

		} else {
			log.Fatal()
		}
	}

	return trainings
}
