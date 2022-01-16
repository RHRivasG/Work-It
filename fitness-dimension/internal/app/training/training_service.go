package app

import (
	"fitness-dimension/internal/app/training/commands"

	"fitness-dimension/internal/core/training"
	"fitness-dimension/internal/core/training/entities"
	"fitness-dimension/internal/core/training/values"
)

type TrainingService struct {
	Publisher  TrainingPublisher
	Repository TrainingRepository
}

func (s *TrainingService) Handle(c interface{}) (interface{}, error) {
	switch c.(type) {
	case commands.CreateTraining:
		command := c.(commands.CreateTraining)

		trainerID := values.TrainerID{Value: command.TrainerID}
		name := values.TrainingName{Value: command.Name}
		description := values.TrainingDescription{Value: command.Description}
		categories := values.TrainingTaxonomies{Values: command.Categories}

		t, _ := training.CreateTraining(categories, trainerID, name, description)
		for _, i := range t.GetEvents() {
			s.Publisher.Publish(i)
		}

		return t.ID.Value.String(), nil

	case commands.UpdateTraining:
		command := c.(commands.UpdateTraining)

		trainerID := values.TrainerID{Value: command.TrainerID}
		name := values.TrainingName{Value: command.Name}
		description := values.TrainingDescription{Value: command.Description}
		categories := values.TrainingTaxonomies{Values: command.Categories}

		t, err := s.Repository.Get(command.ID.String())
		if err != nil {
			return nil, err
		}

		t.Update(categories, trainerID, name, description)
		for _, i := range t.GetEvents() {
			err := s.Publisher.Publish(i)
			if err != nil {
				return nil, err
			}
		}

	case commands.DeleteTraining:
		command := c.(commands.DeleteTraining)

		t, err := s.Repository.Get(command.ID.String())
		if err != nil {
			return nil, err
		}

		t.Destroy()
		for _, i := range t.GetEvents() {
			err := s.Publisher.Publish(i)
			if err != nil {
				return nil, err
			}
		}

	case commands.CreateTrainingVideo:
		command := c.(commands.CreateTrainingVideo)

		filename := values.TrainingVideoName{Value: command.Name}
		video := values.TrainingVideoBuffer{Value: command.Video}
		ext := values.TrainingVideoExt{Value: command.Ext}

		t, err := s.Repository.Get(command.TrainingID.String())
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

	case commands.UpdateTrainingVideo:
		command := c.(commands.UpdateTrainingVideo)

		filename := values.TrainingVideoName{Value: command.Name}
		video := values.TrainingVideoBuffer{Value: command.Video}
		ext := values.TrainingVideoExt{Value: command.Ext}

		t, err := s.Repository.Get(command.TrainingID.String())
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

	case commands.DeleteTrainingVideo:
		command := c.(commands.DeleteTrainingVideo)

		t, err := s.Repository.Get(command.TrainingID.String())
		if err != nil {
			return nil, err
		}

		t.DestroyVideo()
		for _, i := range t.GetEvents() {
			err := s.Publisher.Publish(i)
			if err != nil {
				return nil, err
			}
		}
	}
	return nil, nil
}

func (s *TrainingService) Get(id string) (*training.Training, error) {
	return s.Repository.Get(id)
}

func (s *TrainingService) GetAll() ([]training.Training, error) {
	return s.Repository.GetAll()
}

func (s *TrainingService) GetByTrainer(id string) ([]training.Training, error) {
	return s.Repository.GetByTrainer(id)
}

func (s *TrainingService) GetVideo(id string) *entities.TrainingVideo {
	return s.Repository.GetVideo(id)
}

func (s *TrainingService) GetVideoMetadata(id string) *entities.TrainingVideo {
	return s.Repository.GetVideoMetadata(id)
}
