package controllers

import (
	"bytes"
	"encoding/base64"
	"encoding/json"
	"fitness-dimension/application/trainings"
	"fitness-dimension/application/trainings/commands"
	"fitness-dimension/service/app/auth"
	"fitness-dimension/service/app/helpers"
	"fitness-dimension/service/app/models"
	"io/ioutil"
	"log"
	"net/http"
	"strings"

	"github.com/golang-jwt/jwt"
	"github.com/google/uuid"
	"github.com/labstack/echo/v4"
)

type TrainingHttpController struct {
	Service trainings.TrainingService
}

func (c *TrainingHttpController) Get(ctx echo.Context) error {
	id := ctx.Param("id")
	training := c.Service.Get(id)
	trainingDto := helpers.TranformTrainingToDto(training)
	return ctx.JSON(http.StatusOK, trainingDto)
}

func (c *TrainingHttpController) GetAll(ctx echo.Context) error {
	trainings := c.Service.GetAll()
	var trainingsDto []models.Training
	for _, t := range trainings {
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

	_, err := c.Service.Handle(command)
	if err != nil {
		return err
	}

	return ctx.String(http.StatusCreated, "Training created")
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

func (c *TrainingHttpController) GetVideo(w http.ResponseWriter, r *http.Request) {

	reqBody, err := ioutil.ReadAll(r.Body)
	if err != nil {
		log.Fatal(err)
	}

	command := commands.UpdateTrainingVideo{}
	json.Unmarshal(reqBody, &command)
	c.Service.Handle(command)
}

func (c *TrainingHttpController) CreateVideo(ctx echo.Context) error {
	var body struct {
		Name  string `json:"name"`
		Ext   string `json:"ext"`
		Video string `json:"buff"`
	}
	if err := ctx.Bind(&body); err != nil {
		return err
	}

	reader := base64.NewDecoder(base64.StdEncoding, strings.NewReader(body.Video))
	buff := bytes.Buffer{}
	_, err := buff.ReadFrom(reader)
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
		Video:      buff.Bytes(),
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
