package controllers

import (
	"encoding/json"
	"fitness-dimension/application/trainings"
	"fitness-dimension/application/trainings/commands"
	"io/ioutil"
	"log"
	"net/http"
)

type TrainingHttpController struct {
	Service trainings.TrainingService
}

func (c *TrainingHttpController) Get(w http.ResponseWriter, r *http.Request) {
}

func (c *TrainingHttpController) GetAll(w http.ResponseWriter, r *http.Request) {
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

	command := commands.UpdateTraining{}
	json.Unmarshal(reqBody, &command)
	c.Service.Handle(command)
}

func (c *TrainingHttpController) Delete(w http.ResponseWriter, r *http.Request) {
	reqBody, err := ioutil.ReadAll(r.Body)
	if err != nil {
		log.Fatal(err)
	}

	command := commands.DeleteTraining{}
	json.Unmarshal(reqBody, &command)
	c.Service.Handle(command)
}
