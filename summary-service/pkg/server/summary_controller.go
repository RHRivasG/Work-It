package server

import (
	"log"
	"net/http"
	"summary-service/internal/application"
	"summary-service/internal/application/commands"
	"time"

	"github.com/google/uuid"
	"github.com/labstack/echo/v4"
)

type SummaryController struct {
	Service application.SummaryService
}

func (c *SummaryController) Get(ctx echo.Context) error {
	id := ctx.Param("id")
	routineId, err := uuid.Parse(id)
	if err != nil {
		log.Println(err)
		return err
	}

	summaryDto := c.Service.Get(routineId)
	return ctx.JSON(http.StatusOK, summaryDto)
}

func (c *SummaryController) Create(ctx echo.Context) error {
	id := ctx.Param("id")
	routineId, err := uuid.Parse(id)
	if err != nil {
		log.Println(err)
		return err
	}

	command := commands.CreateSummary{Routine: routineId}
	if err := command.Execute(c.Service); err != nil {
		log.Println(err)
		return err
	}

	return ctx.String(http.StatusOK, "Summary created")
}

func (c *SummaryController) Update(ctx echo.Context) error {
	id := ctx.Param("id")
	routineId, err := uuid.Parse(id)
	if err != nil {
		log.Println(err)
		return err
	}

	var partialCommand struct {
		T string `json:"time"`
	}
	ctx.Bind(&partialCommand)

	summaryTime, err := time.ParseDuration(partialCommand.T)
	if err != nil {
		log.Println(err)
		return err
	}

	command := commands.UpdateSummary{
		Routine: routineId,
		Time:    summaryTime,
	}

	if err := command.Execute(c.Service); err != nil {
		log.Println(err)
		return err
	}

	return ctx.String(http.StatusOK, "Summary updated")
}
