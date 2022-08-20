package server

import (
	"log"
	"training-service/internal/application"
	"training-service/internal/core"
	"training-service/internal/core/entities"
	"training-service/internal/core/values"

	"github.com/go-pg/pg/v10"
	"github.com/google/uuid"
)

type PgTrainingRepository struct {
	application.TrainingRepository
	DB *pg.DB
}

func (r PgTrainingRepository) Get(id string) (*core.Training, error) {
	var trainingModel Training
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

	return &core.Training{
		ID:          values.TrainingID{Value: trainingId},
		Name:        values.TrainingName{Value: trainingModel.Name},
		Description: values.TrainingDescription{Value: trainingModel.Description},
		TrainerID:   values.TrainerID{Value: trainingModel.TrainerID},
		Categories:  values.TrainingTaxonomies{Values: trainingModel.Categories},
		Video:       nil,
	}, nil
}

func (r PgTrainingRepository) GetByTrainer(id string) ([]core.Training, error) {
	var trainingList []Training
	err := r.DB.Model().Table("trainings").Where("trainer_id = ?", id).Select(&trainingList)

	if err != nil && err != pg.ErrNoRows {
		return nil, err
	}

	if err == pg.ErrNoRows {
		return nil, nil
	}

	var trainings []core.Training
	for _, trainingItem := range trainingList {

		trainingId, err := uuid.Parse(trainingItem.ID)
		if err != nil {
			return nil, err
		}

		trainings = append(trainings, core.Training{
			ID:          values.TrainingID{Value: trainingId},
			TrainerID:   values.TrainerID{Value: trainingItem.TrainerID},
			Name:        values.TrainingName{Value: trainingItem.Name},
			Description: values.TrainingDescription{Value: trainingItem.Description},
			Categories:  values.TrainingTaxonomies{Values: trainingItem.Categories},
			Video:       nil,
		})
	}

	return trainings, nil

}

func (r PgTrainingRepository) GetAll() ([]core.Training, error) {

	var trainingList []Training
	err := r.DB.Model().Table("trainings").Select(&trainingList)

	if err != nil && err != pg.ErrNoRows {
		return nil, err
	}

	if err == pg.ErrNoRows {
		return nil, nil
	}

	var trainings []core.Training
	for _, trainingItem := range trainingList {
		id, err := uuid.Parse(trainingItem.ID)
		if err != nil {
			return nil, err
		}

		trainings = append(trainings, core.Training{
			ID:          values.TrainingID{Value: id},
			TrainerID:   values.TrainerID{Value: trainingItem.TrainerID},
			Name:        values.TrainingName{Value: trainingItem.Name},
			Description: values.TrainingDescription{Value: trainingItem.Description},
			Categories:  values.TrainingTaxonomies{Values: trainingItem.Categories},
			Video:       nil,
		})
	}

	return trainings, nil
}

func (r PgTrainingRepository) GetVideo(id string) *entities.TrainingVideo {
	var video TrainingVideo
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
		ID:   values.TrainingVideoID{Value: videoID},
		Name: values.TrainingVideoName{Value: video.Name},
		Ext:  values.TrainingVideoExt{Value: video.Ext},
		Buff: values.TrainingVideoBuffer{Value: video.Buff},
	}
}

func (r PgTrainingRepository) GetVideoMetadata(id string) *entities.TrainingVideo {
	var video TrainingVideoMetadata
	err := r.DB.Model().Table("videos").
		Where("training_id = ?", id).
		ColumnExpr("id, name, ext, length(buff) AS length_video").
		Select(&video)
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
		ID:     values.TrainingVideoID{Value: videoID},
		Name:   values.TrainingVideoName{Value: video.Name},
		Ext:    values.TrainingVideoExt{Value: video.Ext},
		Length: values.TrainingVideoLength{Value: video.Length},
	}
}
