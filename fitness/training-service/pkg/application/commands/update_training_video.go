package commands

import (
	"training-service/pkg/application"
	"training-service/pkg/core/vo"

	"github.com/google/uuid"
)

type UpdateTrainingVideo struct {
	application.TrainingCommand
	TrainingID uuid.UUID
	ID         uuid.UUID
	Name       string
	Ext        string
	Video      []byte
}

func (c *UpdateTrainingVideo) Execute(s *application.TrainingService) (interface{}, error) {

	filename := vo.TrainingVideoName{Value: c.Name}
	video := vo.TrainingVideoBuffer{Value: c.Video}
	ext := vo.TrainingVideoExt{Value: c.Ext}

	t, err := s.Repository.Get(c.TrainingID.String())
	if err != nil {
		return nil, err
	}

	t.UpdateVideo(filename, video, ext)

	for _, i := range t.GetEvents() {
		err := s.Publisher.Publish(i)
		if err != nil {
			return nil, err
		}
	}

	return nil, nil
}
