package controllers

import (
	"encoding/json"
	"fitness-dimension/application/routines"
	"fitness-dimension/application/routines/commands"
	"io/ioutil"
	"log"
	"net/http"
)

type RoutineHttpController struct {
	Service routines.RoutineService
}

func (c *RoutineHttpController) Get(w http.ResponseWriter, r *http.Request) {
}

func (c *RoutineHttpController) GetAll(w http.ResponseWriter, r *http.Request) {
}

func (c *RoutineHttpController) Create(w http.ResponseWriter, r *http.Request) {

	reqBody, err := ioutil.ReadAll(r.Body)
	if err != nil {
		log.Fatal(err)
	}

	command := commands.CreateRoutine{}
	json.Unmarshal(reqBody, &command)
	c.Service.Handle(command)
}

func (c *RoutineHttpController) Update(w http.ResponseWriter, r *http.Request) {
}

func (c *RoutineHttpController) Delete(w http.ResponseWriter, r *http.Request) {
}

func (c *RoutineHttpController) AddTraining(w http.ResponseWriter, r *http.Request) {
}

func (c *RoutineHttpController) RemoveTraining(w http.ResponseWriter, r *http.Request) {
}
