package training

import (
	"fitness-dimension/core/trainings/events"
	entities "fitness-dimension/core/trainings/training/entities"
	valuesObjects "fitness-dimension/core/trainings/training/values-objects"
	"fmt"

	"github.com/google/uuid"
)

type Training struct {
	ID            valuesObjects.TrainingID
	Categories    valuesObjects.TrainingTaxonomies
	TrainerID     valuesObjects.TrainerID
	Name          valuesObjects.TrainingName
	Description   valuesObjects.TrainingDescription
	Video         *entities.TrainingVideo
	eventRecorder []interface{}
}

func (t *Training) GetEvents() []interface{} {
	return t.eventRecorder
}

func (t *Training) AddEvent(event interface{}) {
	t.eventRecorder = append(t.eventRecorder, event)
}

func CreateTraining(
	categories valuesObjects.TrainingTaxonomies,
	trainerID valuesObjects.TrainerID,
	name valuesObjects.TrainingName,
	description valuesObjects.TrainingDescription,
) (Training, error) {

	id := valuesObjects.TrainingID{Value: uuid.New()}

	t := Training{
		ID:          id,
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
	video valuesObjects.TrainingVideoBuffer,
	ext valuesObjects.TrainingVideoExt,
) {

	v := entities.CreateVideo(filename, ext, video)
	t.Video = &v
	fmt.Println(events.TrainingVideoCreated{
		ID:   v.ID,
		Name: v.Name,
		Ext:  v.Ext,
		Buff: v.Buff,
	})
	t.AddEvent(events.TrainingVideoCreated{
		ID:         v.ID,
		Name:       v.Name,
		Ext:        v.Ext,
		Buff:       v.Buff,
		TrainingID: t.ID,
	})
}

func (t *Training) UpdateVideo(
	filename valuesObjects.TrainingVideoName,
	video valuesObjects.TrainingVideoBuffer,
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
	if t.Video != nil {
		t.AddEvent(events.TrainingVideoDeleted{ID: t.Video.ID})
		t.Video = nil
	}
}

func (t *Training) Update(
	categories valuesObjects.TrainingTaxonomies,
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
