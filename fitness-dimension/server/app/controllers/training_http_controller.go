package controllers

import (
	"fitness-dimension/application/trainings"
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
}

func (c *TrainingHttpController) Update(w http.ResponseWriter, r *http.Request) {
}

func (c *TrainingHttpController) Delete(w http.ResponseWriter, r *http.Request) {
}
