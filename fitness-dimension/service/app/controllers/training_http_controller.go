package controllers

import (
	"encoding/base64"
	"fitness-dimension/application/trainings"
	"fitness-dimension/application/trainings/commands"
	"fitness-dimension/service/app/auth"
	"fitness-dimension/service/app/helpers"
	"fmt"
	"log"
	"net/http"

	"github.com/golang-jwt/jwt"
	"github.com/google/uuid"
	"github.com/labstack/echo/v4"
)

type TrainingHttpController struct {
	Service trainings.TrainingService
}

func (c *TrainingHttpController) Get(ctx echo.Context) error {
	id := ctx.Param("id")
	training, err := c.Service.Get(id)
	if err != nil {
		fmt.Println(err)
		return err
	}
	return ctx.JSON(http.StatusOK, training)
}

func (c *TrainingHttpController) GetByTrainer(ctx echo.Context) error {
	trainer := ctx.Get("user").(*jwt.Token)
	claims := trainer.Claims.(*auth.JwtWorkItClaims)
	trainerId := claims.Subject
	if !helpers.Contains(claims.Roles, "trainer") {
		return echo.ErrUnauthorized
	}

	trainingsList, err := c.Service.GetByTrainer(trainerId)
	if err != nil {
		fmt.Println(err)
		return err
	}

	var trainingsDto []trainings.TrainingDto
	for _, t := range trainingsList {
		trainingsDto = append(trainingsDto, helpers.TranformTrainingToDto(t))
	}
	return ctx.JSON(http.StatusOK, trainingsDto)
}

func (c *TrainingHttpController) GetAll(ctx echo.Context) error {
	trainingsList, err := c.Service.GetAll()
	if err != nil {
		fmt.Println(err)
		return err
	}

	var trainingsDto []trainings.TrainingDto
	for _, t := range trainingsList {
		trainingsDto = append(trainingsDto, helpers.TranformTrainingToDto(t))
	}
	return ctx.JSON(http.StatusOK, trainingsDto)
}

func (c *TrainingHttpController) Create(ctx echo.Context) error {

	trainer := ctx.Get("user").(*jwt.Token)
	claims := trainer.Claims.(*auth.JwtWorkItClaims)
	trainerId := claims.Subject
	if !helpers.Contains(claims.Roles, "trainer") {
		return echo.ErrUnauthorized
	}

	var partialCommand struct {
		Name        string   `json:"name"`
		Description string   `json:"description"`
		Categories  []string `json:"categories"`
	}

	if err := ctx.Bind(&partialCommand); err != nil {
		return err
	}

	command := commands.CreateTraining{
		Name:        partialCommand.Name,
		Description: partialCommand.Description,
		Categories:  partialCommand.Categories,
		TrainerID:   trainerId,
	}

	trainingId, err := c.Service.Handle(command)
	if err != nil {
		return err
	}

	return ctx.String(http.StatusCreated, trainingId.(string))
}

func (c *TrainingHttpController) Update(ctx echo.Context) error {

	id := ctx.Param("id")
	trainingId, err := uuid.Parse(id)
	if err != nil {
		return err
	}

	trainer := ctx.Get("user").(*jwt.Token)
	claims := trainer.Claims.(*auth.JwtWorkItClaims)
	trainerId := claims.Subject
	if !helpers.Contains(claims.Roles, "trainer") {
		return echo.ErrUnauthorized
	}

	var partialCommand struct {
		Name        string   `json:"name"`
		Description string   `json:"description"`
		Categories  []string `json:"categories"`
	}

	if err = ctx.Bind(&partialCommand); err != nil {
		return err
	}

	command := commands.UpdateTraining{
		ID:          trainingId,
		Name:        partialCommand.Name,
		Description: partialCommand.Description,
		Categories:  partialCommand.Categories,
		TrainerID:   trainerId,
	}

	_, err = c.Service.Handle(command)
	if err != nil {
		return err
	}

	return ctx.String(http.StatusOK, "Training updated")
}

func (c *TrainingHttpController) Delete(ctx echo.Context) error {

	id := ctx.Param("id")
	trainingId, err := uuid.Parse(id)
	if err != nil {
		return err
	}

	command := commands.DeleteTraining{ID: trainingId}
	c.Service.Handle(command)
	return ctx.String(http.StatusOK, "Training deleted")
}

func (c *TrainingHttpController) GetVideo(ctx echo.Context) error {

	id := ctx.Param("id")

	video := c.Service.GetVideo(id)

	if video == nil {
		return ctx.NoContent(http.StatusNotFound)
	}

	return ctx.String(200, base64.StdEncoding.EncodeToString(video.Buff.Value))
}

func (c *TrainingHttpController) CreateVideo(ctx echo.Context) error {
	var body struct {
		Name  string `json:"name"`
		Ext   string `json:"ext"`
		Video string `json:"video"`
	}
	if err := ctx.Bind(&body); err != nil {
		return err
	}

	buff, err := base64.StdEncoding.DecodeString(body.Video)
	if err != nil {
		log.Fatal(err)
		return err
	}

	id := ctx.Param("id")
	trainingId, err := uuid.Parse(id)
	if err != nil {
		log.Fatal(err)
		return err
	}

	command := commands.CreateTrainingVideo{
		TrainingID: trainingId,
		Name:       body.Name,
		Ext:        body.Ext,
		Video:      buff,
	}
	_, err = c.Service.Handle(command)
	if err != nil {
		return err
	}

	return ctx.String(http.StatusCreated, "Video created")
}

func (c *TrainingHttpController) DeleteVideo(ctx echo.Context) error {

	id := ctx.Param("id")
	trainingId, err := uuid.Parse(id)
	if err != nil {
		log.Fatal(err)
	}

	command := commands.DeleteTrainingVideo{TrainingID: trainingId}
	c.Service.Handle(command)

	return ctx.String(http.StatusOK, "Video deleted")
}
