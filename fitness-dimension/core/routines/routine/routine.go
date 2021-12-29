package routine

import (
	"fitness-dimension/core/routines/events"
	valuesObjects "fitness-dimension/core/routines/routine/values-objects"

	"github.com/google/uuid"
)

type Routine struct {
	ID            valuesObjects.RoutineID
	Name          valuesObjects.RoutineName
	UserID        valuesObjects.RoutineUserID
	TrainingsID   valuesObjects.RoutineTrainingIDs
	Description   valuesObjects.RoutineDescription
	eventRecorder []interface{}
}

func (r *Routine) GetEvents() []interface{} {
	return r.eventRecorder
}

func (r *Routine) AddEvent(event interface{}) {
	r.eventRecorder = append(r.eventRecorder, event)
}

func CreateRoutine(
	name valuesObjects.RoutineName,
	userID valuesObjects.RoutineUserID,
	trainings valuesObjects.RoutineTrainingIDs,
	description valuesObjects.RoutineDescription,
) Routine {

	id := valuesObjects.RoutineID{Value: uuid.New()}

	routine := Routine{
		ID:          id,
		Name:        name,
		UserID:      userID,
		TrainingsID: trainings,
		Description: description,
	}

	routine.AddEvent(events.RoutineCreated{
		ID:          routine.ID,
		Name:        routine.Name,
		UserID:      routine.UserID,
		TrainingsID: routine.TrainingsID,
		Description: routine.Description,
	})

	return routine
}

func (r *Routine) AddTraining(trainingID valuesObjects.RoutineTrainingID) {
	r.TrainingsID.Values = append(r.TrainingsID.Values, trainingID.Value)
	r.AddEvent(events.TrainingAdded{
		ID:         r.ID,
		TrainingID: trainingID,
	})
}

func (r *Routine) RemoveTraining(trainingID valuesObjects.RoutineTrainingID) {

	trainings := valuesObjects.RoutineTrainingIDs{}
	for _, id := range r.TrainingsID.Values {
		if id != trainingID.Value {
			trainings.Values = append(trainings.Values, id)
		}
	}

	r.TrainingsID = trainings

	r.AddEvent(events.TrainingRemoved{
		ID:         r.ID,
		TrainingID: trainingID,
	})
}

func (r *Routine) Update(
	name valuesObjects.RoutineName,
	userID valuesObjects.RoutineUserID,
	trainings valuesObjects.RoutineTrainingIDs,
	description valuesObjects.RoutineDescription,
) {

	r.Name = name
	r.UserID = userID
	r.TrainingsID = trainings
	r.Description = description

	r.AddEvent(events.RoutineUpdated{
		Name:        r.Name,
		UserID:      r.UserID,
		TrainingsID: r.TrainingsID,
		Description: r.Description,
	})
}

func (r *Routine) Destroy() {
	r.AddEvent(events.RoutineDeleted{ID: r.ID})
}
