package routine

import (
	"fitness-dimension/internal/core/routine/events"
	"fitness-dimension/internal/core/routine/values"

	"github.com/google/uuid"
)

type Routine struct {
	ID            values.RoutineID
	Name          values.RoutineName
	UserID        values.RoutineUserID
	TrainingsID   values.RoutineTrainingIDs
	Description   values.RoutineDescription
	eventRecorder []interface{}
}

func (r *Routine) GetEvents() []interface{} {
	return r.eventRecorder
}

func (r *Routine) AddEvent(event interface{}) {
	r.eventRecorder = append(r.eventRecorder, event)
}

func CreateRoutine(
	name values.RoutineName,
	userID values.RoutineUserID,
	trainings values.RoutineTrainingIDs,
	description values.RoutineDescription,
) (*Routine, error) {

	id, err := values.NewRoutineID(uuid.New())
	if err != nil {
		return nil, err
	}

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

	return &routine, nil
}

func (r *Routine) AddTraining(trainingID values.RoutineTrainingID) error {

	for _, existingId := range r.TrainingsID.Values() {
		if existingId == trainingID.Value() {
			return nil
		}
	}

	trainings := append(r.TrainingsID.Values(), trainingID.Value())

	var errs []error
	lastTraining, err := values.NewRoutineTrainingOrder(len(trainings))
	errs = append(errs, err)

	r.TrainingsID, err = values.NewRoutineTrainingIDs(trainings)
	errs = append(errs, err)

	for _, err := range errs {
		if err != nil {
			return err
		}
	}

	r.AddEvent(events.TrainingAdded{
		ID:         r.ID,
		TrainingID: trainingID,
		Order:      lastTraining,
	})

	return nil
}

func (r *Routine) RemoveTraining(trainingID values.RoutineTrainingID) error {

	var trainings []uuid.UUID
	for _, id := range r.TrainingsID.Values() {
		if id != trainingID.Value() {
			trainings = append(trainings, id)
		}
	}

	var err error
	r.TrainingsID, err = values.NewRoutineTrainingIDs(trainings)
	if err != nil {
		return err
	}

	r.AddEvent(events.TrainingRemoved{
		ID:         r.ID,
		TrainingID: trainingID,
	})

	return nil
}

func (r *Routine) Update(
	name values.RoutineName,
	userID values.RoutineUserID,
	trainings values.RoutineTrainingIDs,
	description values.RoutineDescription,
) {

	r.Name = name
	r.UserID = userID
	r.TrainingsID = trainings
	r.Description = description

	r.AddEvent(events.RoutineUpdated{
		ID:          r.ID,
		Name:        r.Name,
		UserID:      r.UserID,
		TrainingsID: r.TrainingsID,
		Description: r.Description,
	})
}

func (r *Routine) Destroy() {
	r.AddEvent(events.RoutineDeleted{ID: r.ID})
}
