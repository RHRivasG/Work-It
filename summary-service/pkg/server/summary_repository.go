package server

import (
	"summary-service/internal/application"

	"github.com/go-pg/pg/v10"
	"github.com/google/uuid"
)

type PgSummaryRepository struct {
	DB *pg.DB
}

func (r PgSummaryRepository) Get(routineId uuid.UUID) application.SummaryDto {
	var summary Summary
	r.DB.Model(&summary).Where("routine = ?", routineId.String()).Select(&summary)
	return application.SummaryDto{
		Routine: summary.Routine.String(),
		MinTime: summary.Mintime.String(),
		MaxTime: summary.Maxtime.String(),
	}
}
