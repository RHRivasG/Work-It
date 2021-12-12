package training

import (
	"fitness-dimension-core/trainings/events"
	entities "fitness-dimension-core/trainings/training/entities"
	valuesObjects "fitness-dimension-core/trainings/training/values-objects"
)

type Training struct {
	ID            valuesObjects.TrainingID
	Categories    []valuesObjects.TrainingTaxonomy
	TrainerID     valuesObjects.TrainerID
	Name          valuesObjects.TrainingName
	Description   valuesObjects.TrainingDescription
	Video         *entities.TrainingVideo
	eventRecorder []interface{}
}

func (t *Training) AddEvent(event interface{}) {
	t.eventRecorder = append(t.eventRecorder, event)
}

func CreateTraining(
	categories []valuesObjects.TrainingTaxonomy,
	trainerID valuesObjects.TrainerID,
	name valuesObjects.TrainingName,
	description valuesObjects.TrainingDescription,
) (Training, error) {

	t := Training{
		Categories:  categories,
		TrainerID:   trainerID,
		Name:        name,
		Description: description,
	}

	t.AddEvent(events.TrainingCreated{
		ID:          t.ID,
		Categories:  t.Categories,
		TrainerID:   t.TrainerID,
		Name:        t.Name,
		Description: t.Description,
	})

	return t, nil
}

func (t *Training) SetVideo(
	filename valuesObjects.TrainingVideoName,
	video valuesObjects.TrainingVideoVideo,
	ext valuesObjects.TrainingVideoExt,
) {

	v := entities.CreateVideo(filename, ext, video)
	t.Video = &v
	t.AddEvent(events.TrainingVideoCreated{
		ID:    v.ID,
		Name:  v.Name,
		Ext:   v.Ext,
		Video: v.Buff,
	})
}

func (t *Training) UpdateVideo(
	filename valuesObjects.TrainingVideoName,
	video valuesObjects.TrainingVideoVideo,
	ext valuesObjects.TrainingVideoExt,
) {
	t.Video.Update(filename, ext, video)
	t.AddEvent(events.TrainingVideoUpdated{
		ID:    t.Video.ID,
		Name:  filename,
		Ext:   ext,
		Video: video,
	})
}

func (t *Training) DestroyVideo() {
	t.Video = nil
	t.AddEvent(events.TrainingVideoDeleted{ID: t.Video.ID})
}

func (t *Training) Update(
	categories []valuesObjects.TrainingTaxonomy,
	trainerID valuesObjects.TrainerID,
	name valuesObjects.TrainingName,
	description valuesObjects.TrainingDescription,
) {

	t.Categories = categories
	t.TrainerID = trainerID
	t.Name = name
	t.Description = description

	t.AddEvent(events.TrainingUpdated{
		ID:          t.ID,
		Categories:  categories,
		TrainerID:   trainerID,
		Name:        name,
		Description: description,
	})
}

func (t *Training) Destroy() {
	t.DestroyVideo()
	t.AddEvent(events.TrainingDeleted{ID: t.ID})
}
