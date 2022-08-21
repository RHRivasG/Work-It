package core

import (
	"training-service/internal/core/entities"
	"training-service/internal/core/events"
	"training-service/internal/core/vo"

	"github.com/google/uuid"
)

type Training struct {
	ID            vo.TrainingID
	Categories    vo.TrainingTaxonomies
	TrainerID     vo.TrainerID
	Name          vo.TrainingName
	Description   vo.TrainingDescription
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
	categories vo.TrainingTaxonomies,
	trainerID vo.TrainerID,
	name vo.TrainingName,
	description vo.TrainingDescription,
) (Training, error) {

	id := vo.TrainingID{Value: uuid.New()}

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
	filename vo.TrainingVideoName,
	video vo.TrainingVideoBuffer,
	ext vo.TrainingVideoExt,
) {

	v := entities.CreateVideo(filename, ext, video)
	t.Video = &v
	t.AddEvent(events.TrainingVideoCreated{
		ID:         v.ID,
		Name:       v.Name,
		Ext:        v.Ext,
		Buff:       v.Buff,
		TrainingID: t.ID,
	})
}

func (t *Training) UpdateVideo(
	filename vo.TrainingVideoName,
	video vo.TrainingVideoBuffer,
	ext vo.TrainingVideoExt,
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
	categories vo.TrainingTaxonomies,
	trainerID vo.TrainerID,
	name vo.TrainingName,
	description vo.TrainingDescription,
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
