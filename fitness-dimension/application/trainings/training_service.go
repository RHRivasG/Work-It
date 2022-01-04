package trainings

import (
	"fitness-dimension/application/trainings/commands"
	"fitness-dimension/application/trainings/repositories"
	"log"

	"fitness-dimension/core/trainings/training"
	valuesObjects "fitness-dimension/core/trainings/training/values-objects"

	"github.com/google/uuid"
)

type TrainingService struct {
	Publisher       TrainingPublisher
	Repository      repositories.TrainingRepository
	VideoRepository repositories.TrainingVideoRepository
}

func (s *TrainingService) Handle(c interface{}) (interface{}, error) {
	switch c.(type) {
	case commands.CreateTraining:
		command := c.(commands.CreateTraining)

		trainerID := valuesObjects.TrainerID{Value: command.TrainerID}
		name := valuesObjects.TrainingName{Value: command.Name}
		description := valuesObjects.TrainingDescription{Value: command.Description}
		categories := valuesObjects.TrainingTaxonomies{}

		t, _ := training.CreateTraining(categories, trainerID, name, description)
		for _, i := range t.GetEvents() {
			s.Publisher.Publish(i)
		}

	case commands.UpdateTraining:
		command := c.(commands.UpdateTraining)

		trainerID := valuesObjects.TrainerID{Value: command.TrainerID}
		name := valuesObjects.TrainingName{Value: command.Name}
		description := valuesObjects.TrainingDescription{Value: command.Description}
		categories := valuesObjects.TrainingTaxonomies{}

		t := s.Repository.Find(command.ID)

		t.Update(categories, trainerID, name, description)
		for _, i := range t.GetEvents() {
			s.Publisher.Publish(i)
		}

	case commands.DeleteTraining:
		command := c.(commands.DeleteTraining)

		t := s.Repository.Find(command.ID)

		t.Destroy()
		for _, i := range t.GetEvents() {
			s.Publisher.Publish(i)
		}

	case commands.CreateTrainingVideo:
		command := c.(commands.CreateTrainingVideo)

		filename := valuesObjects.TrainingVideoName{Value: command.Name}
		video := valuesObjects.TrainingVideoBuffer{Value: command.Video}
		ext := valuesObjects.TrainingVideoExt{Value: command.Ext}

		t := s.Repository.Find(command.TrainingID)

		t.SetVideo(filename, video, ext)
		for _, i := range t.GetEvents() {
			s.Publisher.Publish(i)
		}

	case commands.UpdateTrainingVideo:
		command := c.(commands.UpdateTrainingVideo)

		filename := valuesObjects.TrainingVideoName{Value: command.Name}
		video := valuesObjects.TrainingVideoBuffer{Value: command.Video}
		ext := valuesObjects.TrainingVideoExt{Value: command.Ext}

		t := s.Repository.Find(command.TrainingID)

		t.UpdateVideo(filename, video, ext)
		for _, i := range t.GetEvents() {
			s.Publisher.Publish(i)
		}

	case commands.DeleteTrainingVideo:
		command := c.(commands.DeleteTrainingVideo)
		t := s.Repository.Find(command.TrainingID)
		t.DestroyVideo()

		s.VideoRepository.Delete(command.ID)
		for _, i := range t.GetEvents() {
			s.Publisher.Publish(i)
		}
	}
	return nil, nil
}

func (s *TrainingService) Get(id string) training.Training {
	trainingId, err := uuid.Parse(id)
	if err != nil {
		log.Fatal(err)
	}

	return s.Repository.Find(trainingId)
}

func (s *TrainingService) GetAll() []training.Training {
	return s.Repository.GetAll()
}

//func (s *TrainingService) GetVideo
