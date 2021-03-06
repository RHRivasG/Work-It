package routine

import (
	app "fitness-dimension/internal/app/routine"
	"fitness-dimension/internal/app/routine/commands"
	"fitness-dimension/pkg/auth"
	"fitness-dimension/pkg/helpers"
	"log"
	"net/http"

	"github.com/golang-jwt/jwt"
	"github.com/google/uuid"
	"github.com/labstack/echo/v4"
)

type RoutineHttpController struct {
	Service app.RoutineService
}

func (c *RoutineHttpController) Get(ctx echo.Context) error {
	id := ctx.Param("id")
	user := ctx.Get("user").(*jwt.Token)
	claims := user.Claims.(*auth.JwtWorkItClaims)
	userId := claims.Subject
	if !helpers.Contains(claims.Roles, "participant") {
		return echo.ErrUnauthorized
	}

	routine, err := c.Service.Get(id)
	if err != nil {
		return err
	}

	routineDto := helpers.TranformRoutineToDto(routine)
	if routineDto.UserID != userId {
		return echo.ErrUnauthorized
	}

	return ctx.JSON(http.StatusOK, routineDto)
}

func (c *RoutineHttpController) GetAll(ctx echo.Context) error {
	user := ctx.Get("user").(*jwt.Token)
	claims := user.Claims.(*auth.JwtWorkItClaims)
	userId := claims.Subject
	if !helpers.Contains(claims.Roles, "participant") {
		return echo.ErrUnauthorized
	}

	routineList, err := c.Service.GetAll(userId)
	if err != nil {
		log.Println(err)
		return err
	}

	var routinesDto []app.RoutineDto
	for _, r := range routineList {
		routinesDto = append(routinesDto, helpers.TranformRoutineToDto(&r))
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

	_, err := command.Execute(&c.Service)
	if err != nil {
		return err
	}

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

	_, err = command.Execute(&c.Service)
	if err != nil {
		return err
	}

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

	_, err = command.Execute(&c.Service)
	if err != nil {
		return err
	}

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

	_, err = command.Execute(&c.Service)
	if err != nil {
		return err
	}

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

	_, err = command.Execute(&c.Service)
	if err != nil {
		return err
	}

	return ctx.String(http.StatusOK, "Training removed")
}
