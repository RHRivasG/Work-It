package commands_test

import (
	"fmt"
	"summary-service/internal/application"
	"summary-service/internal/application/commands"
	"testing"
	"time"

	"github.com/google/uuid"
)

func TestUpdateSummaryBasic(t *testing.T) {
	mockService := application.SummaryService{Publisher: &MockPublisher{}}
	newTime, _ := time.ParseDuration("15m10s")
	command := commands.UpdateSummary{
		Routine: uuid.New(),
		Time:    newTime,
	}
	fmt.Println(command)
	if err := command.Execute(mockService); err != nil {
		t.Errorf("got %q", err)
	}
}
