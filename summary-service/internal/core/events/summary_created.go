package events

import "summary-service/internal/core/vo"

type SummaryCreated struct {
	ID      *vo.SummaryID
	Routine *vo.SummaryRoutine
}
