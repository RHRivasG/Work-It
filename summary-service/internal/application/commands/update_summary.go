package commands

import (
	"summary-service/internal/application"
	"summary-service/internal/core"
	"summary-service/internal/core/vo"
	"time"

	"github.com/google/uuid"
)

type UpdateSummary struct {
	Routine uuid.UUID
	Time    time.Duration
}

func (c *UpdateSummary) Execute(s application.SummaryService) error {

	summaryDto := s.Get(c.Routine)

	var errs []error

	id, err := uuid.Parse(summaryDto.ID)
	errs = append(errs, err)
	routine, err := uuid.Parse(summaryDto.Routine)
	errs = append(errs, err)
	mintime, err := time.ParseDuration(summaryDto.MinTime)
	errs = append(errs, err)
	maxtime, err := time.ParseDuration(summaryDto.MaxTime)
	errs = append(errs, err)

	summaryId, err := vo.NewSummaryID(id)
	errs = append(errs, err)
	summaryRoutine, err := vo.NewSummaryRoutine(routine)
	errs = append(errs, err)
	summaryMintime, err := vo.NewSummaryMinTime(mintime)
	errs = append(errs, err)
	summaryMaxtime, err := vo.NewSummaryMaxTime(maxtime)
	errs = append(errs, err)
	summaryTime, err := vo.NewSummaryTime(c.Time)
	errs = append(errs, err)

	for _, err := range errs {
		if err != nil {
			return err
		}
	}

	summary := &core.Summary{
		ID:      summaryId,
		Routine: summaryRoutine,
		MinTime: summaryMintime,
		MaxTime: summaryMaxtime,
	}

	summary.Update(summaryTime)

	for _, event := range summary.Events() {
		s.Publish(event)
	}

	return nil
}
