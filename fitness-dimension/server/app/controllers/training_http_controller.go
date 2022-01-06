package controllers

import (
	"bytes"
	"encoding/base64"
	"encoding/json"
	"fitness-dimension/application/trainings"
	"fitness-dimension/application/trainings/commands"
	"fmt"
	"io/ioutil"
	"log"
	"net/http"
	"strings"

	"github.com/google/uuid"
	"github.com/gorilla/mux"
)

type TrainingHttpController struct {
	Service trainings.TrainingService
}

func (c *TrainingHttpController) Get(w http.ResponseWriter, r *http.Request) {
	vars := mux.Vars(r)
	id := vars["id"]
	training := c.Service.Get(id)

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(&training)
}

func (c *TrainingHttpController) GetAll(w http.ResponseWriter, r *http.Request) {
	trainings := c.Service.GetAll()
	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(&trainings)
}

func (c *TrainingHttpController) Create(w http.ResponseWriter, r *http.Request) {
	reqBody, err := ioutil.ReadAll(r.Body)
	if err != nil {
		log.Fatal(err)
	}

	command := commands.CreateTraining{}
	json.Unmarshal(reqBody, &command)
	c.Service.Handle(command)
}

func (c *TrainingHttpController) Update(w http.ResponseWriter, r *http.Request) {
	reqBody, err := ioutil.ReadAll(r.Body)
	if err != nil {
		log.Fatal(err)
	}

	vars := mux.Vars(r)
	id := vars["id"]

	trainingId, err := uuid.Parse(id)
	if err != nil {
		return
	}

	command := commands.UpdateTraining{}
	json.Unmarshal(reqBody, &command)
	command.ID = trainingId

	c.Service.Handle(command)
}

func (c *TrainingHttpController) Delete(w http.ResponseWriter, r *http.Request) {
	vars := mux.Vars(r)
	id := vars["id"]

	trainingId, err := uuid.Parse(id)
	if err != nil {
		return
	}

	command := commands.DeleteTraining{}
	command.ID = trainingId
	c.Service.Handle(command)
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

func (c *TrainingHttpController) CreateVideo(w http.ResponseWriter, r *http.Request) {

	reqBody, err := ioutil.ReadAll(r.Body)
	if err != nil {
		log.Fatal(err)
	}

	var body struct {
		Name  string `json:"name"`
		Ext   string `json:"ext"`
		Video string `json:"buff"`
	}
	json.Unmarshal(reqBody, &body)

	reader := base64.NewDecoder(base64.StdEncoding, strings.NewReader(body.Video))
	buff := bytes.Buffer{}
	_, err = buff.ReadFrom(reader)
	if err != nil {
		log.Fatal(err)
	}

	vars := mux.Vars(r)
	id := vars["id"]

	trainingId, err := uuid.Parse(id)
	if err != nil {
		log.Fatal(err)
	}

	command := commands.CreateTrainingVideo{
		TrainingID: trainingId,
		Name:       body.Name,
		Ext:        body.Ext,
		Video:      buff.Bytes(),
	}
	fmt.Println(command)

	c.Service.Handle(command)
}

func (c *TrainingHttpController) DeleteVideo(w http.ResponseWriter, r *http.Request) {
	reqBody, err := ioutil.ReadAll(r.Body)
	if err != nil {
		log.Fatal(err)
	}

	command := commands.DeleteTrainingVideo{}
	json.Unmarshal(reqBody, &command)
	c.Service.Handle(command)
}
