package server

import (
	"encoding/base64"
	"fmt"
	"log"
	"net/http"
	"training-service/internal/auth"
	"training-service/internal/helpers"
	"training-service/pkg/application"
	"training-service/pkg/application/commands"

	"github.com/golang-jwt/jwt"
	"github.com/google/uuid"
	"github.com/labstack/echo/v4"
)

type TrainingHttpController struct {
	Service application.TrainingService
}

func (c *TrainingHttpController) Get(ctx echo.Context) error {
	id := ctx.Param("id")
	training, err := c.Service.Get(id)
	if err != nil {
		fmt.Println(err)
		return err
	}
	trainingsDto := helpers.TranformTrainingToDto(training)
	return ctx.JSON(http.StatusOK, trainingsDto)
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

	var trainingsDto []application.TrainingDto
	for _, t := range trainingsList {
		trainingsDto = append(trainingsDto, helpers.TranformTrainingToDto(&t))
	}
	return ctx.JSON(http.StatusOK, trainingsDto)
}

func (c *TrainingHttpController) GetAll(ctx echo.Context) error {
	trainingsList, err := c.Service.GetAll()
	if err != nil {
		fmt.Println(err)
		return err
	}

	var trainingsDto []application.TrainingDto
	for _, t := range trainingsList {
		trainingsDto = append(trainingsDto, helpers.TranformTrainingToDto(&t))
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

	trainingId, err := command.Execute(&c.Service)
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

	_, err = command.Execute(&c.Service)
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
	_, err = command.Execute(&c.Service)
	if err != nil {
		return err
	}

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

func (c *TrainingHttpController) GetVideoMetadata(ctx echo.Context) error {

	id := ctx.Param("id")

	video := c.Service.GetVideoMetadata(id)
	if video == nil {
		return ctx.NoContent(http.StatusNotFound)
	}

	videoDto := helpers.TransformVideoToDto(video)

	return ctx.JSON(200, videoDto)
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

	_, err = command.Execute(&c.Service)
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
	_, err = command.Execute(&c.Service)
	if err != nil {
		return err
	}

	return ctx.String(http.StatusOK, "Video deleted")
}
