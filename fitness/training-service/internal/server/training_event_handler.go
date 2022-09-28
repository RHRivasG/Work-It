package server

import (
	"fmt"
	"training-service/pkg/core/events"

	"github.com/go-pg/pg/v10"
)

type TrainingEventHandler struct {
	DB *pg.DB
}

func (h *TrainingEventHandler) Handle(e interface{}) error {
	switch e.(type) {
	case events.TrainingCreated:
		event := e.(events.TrainingCreated)
		_, err := h.save(event)
		return err

	case events.TrainingUpdated:
		event := e.(events.TrainingUpdated)
		_, err := h.update(event)
		return err

	case events.TrainingDeleted:
		event := e.(events.TrainingDeleted)
		_, err := h.delete(event)
		return err

	case events.TrainingVideoCreated:
		event := e.(events.TrainingVideoCreated)
		_, err := h.saveVideo(event)
		return err

	case events.TrainingVideoDeleted:
		event := e.(events.TrainingVideoDeleted)
		_, err := h.deleteVideo(event)
		return err

	}

	return nil
}

func (h *TrainingEventHandler) save(event events.TrainingCreated) (*string, error) {
	fmt.Println("Saving training")

	training := &Training{
		ID:          event.ID.Value.String(),
		TrainerID:   event.TrainerID.Value,
		Name:        event.Name.Value,
		Description: event.Description.Value,
		Categories:  event.Categories.Values,
	}
	_, err := h.DB.Model(training).Insert()
	if err != nil {
		return nil, err
	}

	msg := "Training saved"
	return &msg, nil
}

func (h *TrainingEventHandler) update(event events.TrainingUpdated) (*string, error) {
	fmt.Println("Updating training")

	training := &Training{
		ID:          event.ID.Value.String(),
		TrainerID:   event.TrainerID.Value,
		Name:        event.Name.Value,
		Description: event.Description.Value,
		Categories:  event.Categories.Values,
	}
	_, err := h.DB.Model(training).WherePK().Update()
	if err != nil {
		return nil, err
	}

	msg := "Training updated"
	return &msg, nil
}

func (h *TrainingEventHandler) delete(event events.TrainingDeleted) (*string, error) {
	fmt.Println("Deleting training")

	training := &Training{
		ID: event.ID.Value.String(),
	}
	_, err := h.DB.Model(training).WherePK().Delete()
	if err != nil {
		return nil, err
	}

	msg := "Training deleted"
	return &msg, nil
}

func (h *TrainingEventHandler) saveVideo(event events.TrainingVideoCreated) (*string, error) {
	fmt.Println("Saving video")

	video := &TrainingVideo{
		ID:         event.ID.Value.String(),
		Name:       event.Name.Value,
		Ext:        event.Ext.Value,
		Buff:       []byte(event.Buff.Value),
		TrainingID: event.TrainingID.Value.String(),
	}

	var existsVideo TrainingVideo
	err := h.DB.Model().Table("videos").Where("training_id = ?", video.TrainingID).Select(&existsVideo)
	if err != nil && err != pg.ErrNoRows {
		return nil, err
	}
	if &existsVideo.ID != nil {
		_, err := h.DB.Model(&existsVideo).WherePK().Delete()
		if err != nil {
			return nil, err
		}
	}

	_, err = h.DB.Model(video).Insert()
	if err != nil {
		return nil, err
	}

	msg := "Video saved"
	return &msg, nil
}

func (h *TrainingEventHandler) deleteVideo(event events.TrainingVideoDeleted) (*string, error) {
	fmt.Println("Deleting video")

	video := &TrainingVideo{
		ID: event.ID.Value.String(),
	}
	_, err := h.DB.Model(video).WherePK().Delete()
	if err != nil {
		return nil, err
	}

	msg := "Video deleted"
	return &msg, nil
}
