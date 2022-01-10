package controllers

import (
	"fitness-dimension/application/routines"
	"fitness-dimension/application/routines/commands"
	"fitness-dimension/service/app/auth"
	"fitness-dimension/service/app/helpers"
	"fitness-dimension/service/app/models"
	"net/http"

	"github.com/golang-jwt/jwt"
	"github.com/google/uuid"
	"github.com/labstack/echo/v4"
)

type RoutineHttpController struct {
	Service routines.RoutineService
}

func (c *RoutineHttpController) Get(ctx echo.Context) error {
	id := ctx.Param("id")
	routine := c.Service.Get(id)
	routineDto := helpers.TranformRoutineToDto(routine)
	return ctx.JSON(http.StatusOK, routineDto)
}

func (c *RoutineHttpController) GetAll(ctx echo.Context) error {
	user := ctx.Get("user").(*jwt.Token)
	claims := user.Claims.(*auth.JwtWorkItClaims)
	userId := claims.Subject

	if !helpers.Contains(claims.Roles, "participant") {
		return echo.ErrUnauthorized
	}
	routines := c.Service.GetAll(userId)
	var routinesDto []models.Routine
	for _, r := range routines {
		routinesDto = append(routinesDto, helpers.TranformRoutineToDto(r))
	}
	return ctx.JSON(http.StatusOK, routinesDto)
}

func (c *RoutineHttpController) Create(ctx echo.Context) error {

	user := ctx.Get("user").(*jwt.Token)
	claims := user.Claims.(*auth.JwtWorkItClaims)
	userId := claims.Subject
	if !helpers.Contains(claims.Roles, "participant") {
		return echo.ErrUnauthorized
	}

	var partialCommand struct {
		Name        string   `json:"name"`
		Description string   `json:"description"`
		Trainings   []string `json:"trainings"`
	}
	ctx.Bind(&partialCommand)

	command := commands.CreateRoutine{
		UserID:      userId,
		Name:        partialCommand.Name,
		Description: partialCommand.Description,
		TrainingsID: partialCommand.Trainings,
	}
	c.Service.Handle(command)

	return ctx.String(http.StatusCreated, "Routine created")
}

func (c *RoutineHttpController) Update(ctx echo.Context) error {

	id := ctx.Param("id")
	routineId, err := uuid.Parse(id)
	if err != nil {
		return err
	}

	user := ctx.Get("user").(*jwt.Token)
	claims := user.Claims.(*auth.JwtWorkItClaims)
	userId := claims.Subject
	if !helpers.Contains(claims.Roles, "participant") {
		return echo.ErrUnauthorized
	}

	var partialCommand struct {
		Name        string   `json:"name"`
		Description string   `json:"description"`
		Trainings   []string `json:"trainings"`
	}
	ctx.Bind(&partialCommand)

	command := commands.UpdateRoutine{
		ID:          routineId,
		UserID:      userId,
		Name:        partialCommand.Name,
		Description: partialCommand.Description,
		TrainingsID: partialCommand.Trainings,
	}
	c.Service.Handle(command)

	return ctx.String(http.StatusOK, "Routine updated")
}

func (c *RoutineHttpController) Delete(ctx echo.Context) error {
	id := ctx.Param("id")
	routineId, err := uuid.Parse(id)
	if err != nil {
		return err
	}

	command := commands.DeleteRoutine{
		ID: routineId,
	}
	c.Service.Handle(command)

	return ctx.String(http.StatusOK, "Routine deleted")
}

func (c *RoutineHttpController) AddTraining(ctx echo.Context) error {
	id := ctx.Param("id")
	routineId, err := uuid.Parse(id)
	if err != nil {
		return err
	}

	idt := ctx.Param("idt")
	trainingId, err := uuid.Parse(idt)
	if err != nil {
		return err
	}

	command := commands.AddRoutineTraining{
		ID:         routineId,
		TrainingID: trainingId,
	}
	c.Service.Handle(command)
	return ctx.String(http.StatusOK, "Training added")
}

func (c *RoutineHttpController) RemoveTraining(ctx echo.Context) error {
	id := ctx.Param("id")
	routineId, err := uuid.Parse(id)
	if err != nil {
		return err
	}

	idt := ctx.Param("idt")
	trainingId, err := uuid.Parse(idt)
	if err != nil {
		return err
	}

	command := commands.RemoveRoutineTraining{
		ID:         routineId,
		TrainingID: trainingId,
	}
	c.Service.Handle(command)
	return ctx.String(http.StatusOK, "Training removed")
}
