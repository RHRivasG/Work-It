package training

import (
	entities "fitness-dimension/trainings/training/entities"
	valuesObjects "fitness-dimension/trainings/training/values-objects"
)

type Training struct {
	ID          valuesObjects.TrainingID
	Categories  []valuesObjects.TrainingTaxonomy
	TrainerID   valuesObjects.TrainerID
	Name        valuesObjects.TrainingName
	Description valuesObjects.TrainingDescription
	Video       *entities.TrainingVideo
}

type TrainingProperties struct{}
type UpdatedTrainingProperties struct{}

func create(data TrainingProperties)/*(Training, error)*/ {}

func (t *Training) setVideo(filename string, video []byte, ext string) {}

func (t *Training) destroyVideo() {}

func (t *Training) update(data UpdatedTrainingProperties) {}

func (t *Training) destroy() {}

func (t *Training) when() {}

func (t *Training) invariants() {}
