package commands_test

import (
	"fmt"
	"summary-service/internal/application"
	"summary-service/internal/application/commands"
	"summary-service/internal/core/events"
	"testing"

	"github.com/google/uuid"
)

type MockPublisher struct{}

func (m *MockPublisher) Publish(event interface{}) {
	switch event.(type) {
	case events.SummaryCreated:
		fmt.Println("Event summary created published")
	case events.SummaryUpdated:
		fmt.Println("Event summary updated published")
	}
}

func TestCreateSummaryBasic(t *testing.T) {
	mockService := application.SummaryService{Publisher: &MockPublisher{}}
	command := commands.CreateSummary{
		Routine: uuid.New(),
	}
	fmt.Println(command)
	if err := command.Execute(mockService); err != nil {
		t.Errorf("got %q", err)
	}
}
