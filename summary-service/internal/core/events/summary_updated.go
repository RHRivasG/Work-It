package events

import "summary-service/internal/core/vo"

type SummaryUpdated struct {
	ID      *vo.SummaryID
	Routine *vo.SummaryRoutine
	MinTime *vo.SummaryMinTime
	MaxTime *vo.SummaryMaxTime
}
