package application

import "github.com/google/uuid"

type SummaryService struct {
	Publisher  SummaryPublisher
	Repository SummaryRepository
}

func (s *SummaryService) Get(id uuid.UUID) SummaryDto {
	return s.Repository.Get(id)
}

func (s *SummaryService) Publish(event interface{}) {
	s.Publisher.Publish(event)
}
