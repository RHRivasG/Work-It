package application

import (
	"training-service/pkg/core"
	"training-service/pkg/core/entities"
)

type TrainingService struct {
	Publisher  TrainingPublisher
	Repository TrainingRepository
}

func (s *TrainingService) Get(id string) (*core.Training, error) {
	return s.Repository.Get(id)
}

func (s *TrainingService) GetAll() ([]core.Training, error) {
	return s.Repository.GetAll()
}

func (s *TrainingService) GetByTrainer(id string) ([]core.Training, error) {
	return s.Repository.GetByTrainer(id)
}

func (s *TrainingService) GetVideo(id string) *entities.TrainingVideo {
	return s.Repository.GetVideo(id)
}

func (s *TrainingService) GetVideoMetadata(id string) *entities.TrainingVideo {
	return s.Repository.GetVideoMetadata(id)
}
