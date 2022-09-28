package core_test

import (
	"summary-service/internal/core"
	"summary-service/internal/core/events"
	"summary-service/internal/core/vo"
	"testing"
	"time"

	"github.com/google/uuid"
)

func TestCreateSummaryBasic(t *testing.T) {
	routine, _ := vo.NewSummaryRoutine(uuid.New())
	summary, _ := core.CreateSummary(routine)

	if summary.Routine.Value() != routine.Value() {
		t.Errorf("got %q, wanted %q", summary.Routine.Value(), routine.Value())
	}

	event := summary.Events()[0].(events.SummaryCreated)

	if event.ID.Value() != summary.ID.Value() {
		t.Errorf("got %q, wanted %q", event.ID.Value(), summary.ID.Value())
	}

	if event.Routine.Value() != summary.Routine.Value() {
		t.Errorf("got %q, wanted %q", event.Routine.Value(), summary.Routine.Value())
	}

}

func TestUpdateWithoutTime(t *testing.T) {
	id, _ := vo.NewSummaryID(uuid.New())
	routine, _ := vo.NewSummaryRoutine(uuid.New())
	summary := core.Summary{
		ID:      id,
		Routine: routine,
	}

	newTimeDuration, _ := time.ParseDuration("15m10s")
	newTime, _ := vo.NewSummaryTime(newTimeDuration)
	if err := summary.Update(newTime); err != nil {
		t.Errorf("got %q", err)
	}

	if summary.Events() == nil {
		t.Errorf("got a empty array, wanted a summary updated event")
	}

	event := summary.Events()[0].(events.SummaryUpdated)

	if event.MaxTime.Value() != newTimeDuration {
		t.Errorf("got %q, wanted %q", event.ID.Value(), newTimeDuration)
	}
	if event.MinTime.Value() != newTimeDuration {
		t.Errorf("got %q, wanted %q", event.ID.Value(), newTimeDuration)
	}

}

func TestUpdateNewMaxtime(t *testing.T) {
	id, _ := vo.NewSummaryID(uuid.New())
	routine, _ := vo.NewSummaryRoutine(uuid.New())
	maxTimeDuration, _ := time.ParseDuration("10m10s")
	maxtime, _ := vo.NewSummaryMaxTime(maxTimeDuration)
	minTimeDuration, _ := time.ParseDuration("10m10s")
	mintime, _ := vo.NewSummaryMinTime(minTimeDuration)
	summary := core.Summary{
		ID:      id,
		Routine: routine,
		MaxTime: maxtime,
		MinTime: mintime,
	}

	newMaxTimeDuration, _ := time.ParseDuration("15m10s")
	newMaxTime, _ := vo.NewSummaryTime(newMaxTimeDuration)
	summary.Update(newMaxTime)
	event := summary.Events()[0].(events.SummaryUpdated)

	if event.MaxTime.Value() != newMaxTimeDuration {
		t.Errorf("got %q, wanted %q", event.ID.Value(), newMaxTimeDuration)
	}
}

func TestUpdateNewMintime(t *testing.T) {
	id, _ := vo.NewSummaryID(uuid.New())
	routine, _ := vo.NewSummaryRoutine(uuid.New())
	maxTimeDuration, _ := time.ParseDuration("10m10s")
	maxtime, _ := vo.NewSummaryMaxTime(maxTimeDuration)
	minTimeDuration, _ := time.ParseDuration("10m10s")
	mintime, _ := vo.NewSummaryMinTime(minTimeDuration)
	summary := core.Summary{
		ID:      id,
		Routine: routine,
		MaxTime: maxtime,
		MinTime: mintime,
	}

	newMinTimeDuration, _ := time.ParseDuration("5m10s")
	newMinTime, _ := vo.NewSummaryTime(newMinTimeDuration)
	summary.Update(newMinTime)
	event := summary.Events()[0].(events.SummaryUpdated)

	if event.MinTime.Value() != newMinTimeDuration {
		t.Errorf("got %q, wanted %q", event.ID.Value(), newMinTimeDuration)
	}
}
