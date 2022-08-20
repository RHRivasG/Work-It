package commands

import (
	"summary-service/internal/application"
	"summary-service/internal/core"
	"summary-service/internal/core/vo"

	"github.com/google/uuid"
)

type CreateSummary struct {
	Routine uuid.UUID
}

func (c *CreateSummary) Execute(s application.SummaryService) error {

	routine, err := vo.NewSummaryRoutine(c.Routine)
	if err != nil {
		return err
	}

	summary, err := core.CreateSummary(routine)
	if err != nil {
		return err
	}

	for _, event := range summary.Events() {
		s.Publish(event)
	}

	return nil
}
