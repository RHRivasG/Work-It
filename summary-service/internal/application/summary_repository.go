package application

import "github.com/google/uuid"

type SummaryRepository interface {
	Get(uuid.UUID) SummaryDto
}
