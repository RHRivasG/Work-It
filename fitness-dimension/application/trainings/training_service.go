package trainings

import (
	"fitness-dimension/application/trainings/commands"
	"fitness-dimension/application/trainings/repositories"

	"fitness-dimension/core/trainings/training"
	valuesObjects "fitness-dimension/core/trainings/training/values-objects"
)

type TrainingService struct {
	TrainingRepository      repositories.TrainingRepository
	TrainingVideoRepository repositories.TrainingVideoRepository
	Publisher               TrainingPublisher
}

func (s *TrainingService) handle(c interface{}) (interface{}, error) {
	switch c.(type) {
	case commands.CreateTraining:
		command := c.(commands.CreateTraining)

		trainerID := valuesObjects.TrainerID{Value: command.TrainerID}
		name := valuesObjects.TrainingName{Value: command.Name}
		description := valuesObjects.TrainingDescription{Value: command.Description}
		categories := []valuesObjects.TrainingTaxonomy{}

		t, _ := training.CreateTraining(categories, trainerID, name, description)
		for _, i := range t.GetEvents() {
			s.Publisher.Publish(i)
		}

	case commands.UpdateTraining:
		command := c.(commands.UpdateTraining)

		trainerID := valuesObjects.TrainerID{Value: command.TrainerID}
		name := valuesObjects.TrainingName{Value: command.Name}
		description := valuesObjects.TrainingDescription{Value: command.Description}
		categories := []valuesObjects.TrainingTaxonomy{}

		t := s.TrainingRepository.Find(command.ID)

		t.Update(categories, trainerID, name, description)
		for _, i := range t.GetEvents() {
			s.Publisher.Publish(i)
		}

	case commands.DeleteTraining:
		command := c.(commands.DeleteTraining)

		t := s.TrainingRepository.Find(command.ID)

		t.Destroy()
		for _, i := range t.GetEvents() {
			s.Publisher.Publish(i)
		}

	case commands.CreateTrainingVideo:
		command := c.(commands.CreateTrainingVideo)

		filename := valuesObjects.TrainingVideoName{Value: command.Name}
		video := valuesObjects.TrainingVideoVideo{Value: command.Video}
		ext := valuesObjects.TrainingVideoExt{Value: command.Ext}

		t := s.TrainingRepository.Find(command.ID)

		t.SetVideo(filename, video, ext)
		for _, i := range t.GetEvents() {
			s.Publisher.Publish(i)
		}

	case commands.UpdateTrainingVideo:
		command := c.(commands.UpdateTrainingVideo)

		filename := valuesObjects.TrainingVideoName{Value: command.Name}
		video := valuesObjects.TrainingVideoVideo{Value: command.Video}
		ext := valuesObjects.TrainingVideoExt{Value: command.Ext}

		t := s.TrainingRepository.Find(command.TrainingID)

		t.UpdateVideo(filename, video, ext)
		for _, i := range t.GetEvents() {
			s.Publisher.Publish(i)
		}

	case commands.DeleteTrainingVideo:
		command := c.(commands.DeleteTrainingVideo)
		t := s.TrainingRepository.Find(command.TrainingID)
		t.DestroyVideo()

		s.TrainingVideoRepository.Delete(command.ID)
		for _, i := range t.GetEvents() {
			s.Publisher.Publish(i)
		}
	}
	return nil, nil
}
