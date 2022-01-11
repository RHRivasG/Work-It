package trainings

import (
	"fitness-dimension/application/trainings/commands"

	"fitness-dimension/core/trainings/training"
	"fitness-dimension/core/trainings/training/entities"
	valuesObjects "fitness-dimension/core/trainings/training/values-objects"
)

type TrainingService struct {
	Publisher  TrainingPublisher
	Repository TrainingRepository
}

func (s *TrainingService) Handle(c interface{}) (interface{}, error) {
	switch c.(type) {
	case commands.CreateTraining:
		command := c.(commands.CreateTraining)

		trainerID := valuesObjects.TrainerID{Value: command.TrainerID}
		name := valuesObjects.TrainingName{Value: command.Name}
		description := valuesObjects.TrainingDescription{Value: command.Description}
		categories := valuesObjects.TrainingTaxonomies{Values: command.Categories}

		t, _ := training.CreateTraining(categories, trainerID, name, description)
		for _, i := range t.GetEvents() {
			s.Publisher.Publish(i)
		}

		return t.ID.Value.String(), nil

	case commands.UpdateTraining:
		command := c.(commands.UpdateTraining)

		trainerID := valuesObjects.TrainerID{Value: command.TrainerID}
		name := valuesObjects.TrainingName{Value: command.Name}
		description := valuesObjects.TrainingDescription{Value: command.Description}
		categories := valuesObjects.TrainingTaxonomies{Values: command.Categories}

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

		filename := valuesObjects.TrainingVideoName{Value: command.Name}
		video := valuesObjects.TrainingVideoBuffer{Value: command.Video}
		ext := valuesObjects.TrainingVideoExt{Value: command.Ext}

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

		filename := valuesObjects.TrainingVideoName{Value: command.Name}
		video := valuesObjects.TrainingVideoBuffer{Value: command.Video}
		ext := valuesObjects.TrainingVideoExt{Value: command.Ext}

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
