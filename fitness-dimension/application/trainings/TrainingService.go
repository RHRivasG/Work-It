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
}

func (s *TrainingService) handle(c interface{}) {
	switch c.(type) {
	case commands.CreateTraining:
		command := c.(commands.CreateTraining)

		trainerID := valuesObjects.TrainerID{Value: command.TrainerID}
		name := valuesObjects.TrainingName{Value: command.Name}
		description := valuesObjects.TrainingDescription{Value: command.Description}
		categories := []valuesObjects.TrainingTaxonomy{}

		training.CreateTraining(categories, trainerID, name, description)

	case commands.UpdateTraining:
		command := c.(commands.UpdateTraining)

		trainerID := valuesObjects.TrainerID{Value: command.TrainerID}
		name := valuesObjects.TrainingName{Value: command.Name}
		description := valuesObjects.TrainingDescription{Value: command.Description}
		categories := []valuesObjects.TrainingTaxonomy{}

		t := s.TrainingRepository.Find(command.ID)

		t.Update(categories, trainerID, name, description)

	case commands.DeleteTraining:
		command := c.(commands.DeleteTraining)

		t := s.TrainingRepository.Find(command.ID)

		t.Destroy()
		s.TrainingRepository.Delete(t)

	case commands.CreateTrainingVideo:
		command := c.(commands.CreateTrainingVideo)

		filename := valuesObjects.TrainingVideoName{Value: command.Name}
		video := valuesObjects.TrainingVideoVideo{Value: command.Video}
		ext := valuesObjects.TrainingVideoExt{Value: command.Ext}

		t := s.TrainingRepository.Find(command.ID)

		t.SetVideo(filename, video, ext)

	case commands.UpdateTrainingVideo:
		command := c.(commands.UpdateTrainingVideo)

		filename := valuesObjects.TrainingVideoName{Value: command.Name}
		video := valuesObjects.TrainingVideoVideo{Value: command.Video}
		ext := valuesObjects.TrainingVideoExt{Value: command.Ext}

		t := s.TrainingRepository.Find(command.TrainingID)

		t.UpdateVideo(filename, video, ext)

	case commands.DeleteTrainingVideo:
		command := c.(commands.DeleteTrainingVideo)
		t := s.TrainingRepository.Find(command.TrainingID)
		t.DestroyVideo()

		s.TrainingVideoRepository.Delete(command.ID)
	}
}
