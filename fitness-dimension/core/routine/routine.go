package routine

import (
	"fitness-dimension/core/routine/events"
	valueObjects "fitness-dimension/core/routine/values"

	"github.com/google/uuid"
)

type Routine struct {
	ID            valueObjects.RoutineID
	Name          valueObjects.RoutineName
	UserID        valueObjects.RoutineUserID
	TrainingsID   valueObjects.RoutineTrainingIDs
	Description   valueObjects.RoutineDescription
	eventRecorder []interface{}
}

func (r *Routine) GetEvents() []interface{} {
	return r.eventRecorder
}

func (r *Routine) AddEvent(event interface{}) {
	r.eventRecorder = append(r.eventRecorder, event)
}

func CreateRoutine(
	name valueObjects.RoutineName,
	userID valueObjects.RoutineUserID,
	trainings valueObjects.RoutineTrainingIDs,
	description valueObjects.RoutineDescription,
) (*Routine, error) {

	id, err := valueObjects.NewRoutineID(uuid.New())
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

func (r *Routine) AddTraining(trainingID valueObjects.RoutineTrainingID) error {

	for _, existingId := range r.TrainingsID.Values() {
		if existingId == trainingID.Value() {
			return nil
		}
	}

	trainings := append(r.TrainingsID.Values(), trainingID.Value())

	var errs []error
	lastTraining, err := valueObjects.NewRoutineTrainingOrder(len(trainings))
	errs = append(errs, err)

	r.TrainingsID, err = valueObjects.NewRoutineTrainingIDs(trainings)
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

func (r *Routine) RemoveTraining(trainingID valueObjects.RoutineTrainingID) error {

	var trainings []uuid.UUID
	for _, id := range r.TrainingsID.Values() {
		if id != trainingID.Value() {
			trainings = append(trainings, id)
		}
	}

	var err error
	r.TrainingsID, err = valueObjects.NewRoutineTrainingIDs(trainings)
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
	name valueObjects.RoutineName,
	userID valueObjects.RoutineUserID,
	trainings valueObjects.RoutineTrainingIDs,
	description valueObjects.RoutineDescription,
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
