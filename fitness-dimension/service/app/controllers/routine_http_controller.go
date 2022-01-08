package controllers

import (
	"encoding/json"
	"fitness-dimension/application/routines"
	"fitness-dimension/application/routines/commands"
	"io/ioutil"
	"log"
	"net/http"

	"github.com/gorilla/mux"
)

type RoutineHttpController struct {
	Service routines.RoutineService
}

func (c *RoutineHttpController) Get(w http.ResponseWriter, r *http.Request) {
	vars := mux.Vars(r)
	id := vars["id"]
	routine := c.Service.Get(id)

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(&routine)
}

func (c *RoutineHttpController) GetAll(w http.ResponseWriter, r *http.Request) {
	routines := c.Service.GetAll()
	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(&routines)
}

func (c *RoutineHttpController) Create(w http.ResponseWriter, r *http.Request) {
	reqBody, err := ioutil.ReadAll(r.Body)
	if err != nil {
		log.Fatal(err)
	}

	command := commands.CreateRoutine{}
	json.Unmarshal(reqBody, &command)
	c.Service.Handle(command)

	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusCreated)
}

func (c *RoutineHttpController) Update(w http.ResponseWriter, r *http.Request) {
	reqBody, err := ioutil.ReadAll(r.Body)
	if err != nil {
		log.Fatal(err)
	}

	command := commands.UpdateRoutine{}
	json.Unmarshal(reqBody, &command)
	c.Service.Handle(command)
}

func (c *RoutineHttpController) Delete(w http.ResponseWriter, r *http.Request) {
	reqBody, err := ioutil.ReadAll(r.Body)
	if err != nil {
		log.Fatal(err)
	}

	command := commands.DeleteRoutine{}
	json.Unmarshal(reqBody, &command)
	c.Service.Handle(command)
}

func (c *RoutineHttpController) AddTraining(w http.ResponseWriter, r *http.Request) {
	reqBody, err := ioutil.ReadAll(r.Body)
	if err != nil {
		log.Fatal(err)
	}

	command := commands.AddRoutineTraining{}
	json.Unmarshal(reqBody, &command)
	c.Service.Handle(command)
}

func (c *RoutineHttpController) RemoveTraining(w http.ResponseWriter, r *http.Request) {
	reqBody, err := ioutil.ReadAll(r.Body)
	if err != nil {
		log.Fatal(err)
	}

	command := commands.RemoveRoutineTraining{}
	json.Unmarshal(reqBody, &command)
	c.Service.Handle(command)
}
