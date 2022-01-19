package commands

import (
	app "fitness-dimension/internal/app/training"
	"fitness-dimension/internal/core/training/values"

	"github.com/google/uuid"
)

type CreateTrainingVideo struct {
	app.TrainingCommand
	TrainingID uuid.UUID
	Name       string
	Ext        string
	Video      []byte
}

func (c *CreateTrainingVideo) Execute(s *app.TrainingService) (interface{}, error) {
	filename := values.TrainingVideoName{Value: c.Name}
	video := values.TrainingVideoBuffer{Value: c.Video}
	ext := values.TrainingVideoExt{Value: c.Ext}

	t, err := s.Repository.Get(c.TrainingID.String())
	if err != nil {
		return nil, err
	}

	t.SetVideo(filename, video, ext)

	for _, i := range t.GetEvents() {
		err := s.Publisher.Publish(i)
		if err != nil {
			return nil, err
		}
	}

	return nil, nil
}
