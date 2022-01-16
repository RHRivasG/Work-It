package routine

import (
	"context"
	pb "fitness-dimension/pkg/api/proto"
	"fmt"

	"github.com/go-pg/pg/v10"
)

type RoutineApiServer struct {
	pb.UnimplementedRoutineAPIServer
	DB *pg.DB
}

func (s *RoutineApiServer) Save(ctx context.Context, req *pb.RoutineCreated) (*pb.Response, error) {
	fmt.Println("Saving routine")

	routine := &Routine{
		ID:          req.Id,
		Name:        req.Name,
		UserID:      req.UserId,
		Description: req.Description,
	}
	_, err := s.DB.Model(routine).Insert()
	if err != nil {
		return nil, err
	}

	for i, t := range req.TrainingsId {
		rt := &RoutineTraining{
			RoutineID:  routine.ID,
			TrainingID: t,
			Order:      i + 1,
		}
		_, err := s.DB.Model(rt).Insert()
		if err != nil {
			return nil, err
		}
	}

	msg := pb.Response{Msg: "Routine saved"}
	return &msg, nil
}

func (s *RoutineApiServer) Update(ctx context.Context, req *pb.RoutineUpdated) (*pb.Response, error) {
	fmt.Println("Updating routine")

	routine := &Routine{
		ID:          req.Id,
		Name:        req.Name,
		UserID:      req.UserId,
		Description: req.Description,
	}
	_, err := s.DB.Model(routine).WherePK().Update()
	if err != nil {
		return nil, err
	}

	for i, t := range req.TrainingsId {
		rt := &RoutineTraining{
			RoutineID:  routine.ID,
			TrainingID: t,
			Order:      i + 1,
		}
		_, err := s.DB.Model(rt).WherePK().Update()
		if err != nil {
			return nil, err
		}
	}

	msg := pb.Response{Msg: "Routine updated"}
	return &msg, nil
}

func (s *RoutineApiServer) Delete(ctx context.Context, req *pb.RoutineDeleted) (*pb.Response, error) {
	fmt.Println("Deleting routine")

	routine := &Routine{
		ID: req.Id,
	}
	_, err := s.DB.Model(routine).WherePK().Delete()
	if err != nil {
		return nil, err
	}

	msg := pb.Response{Msg: "Routine deleted"}
	return &msg, nil
}

func (s *RoutineApiServer) AddTraining(ctx context.Context, req *pb.TrainingAdded) (*pb.Response, error) {
	fmt.Println("Adding training")

	routineTraining := &RoutineTraining{
		RoutineID:  req.RoutineId,
		TrainingID: req.TrainingId,
		Order:      int(req.Order),
	}
	_, err := s.DB.Model(routineTraining).Insert()
	if err != nil {
		return nil, err
	}

	msg := pb.Response{Msg: "Training added"}
	return &msg, nil
}

func (s *RoutineApiServer) RemoveTraining(ctx context.Context, req *pb.TrainingRemoved) (*pb.Response, error) {
	fmt.Println("Removing training")

	routineTraining := &RoutineTraining{
		RoutineID:  req.RoutineId,
		TrainingID: req.TrainingId,
	}

	s.DB.Model(routineTraining).WherePK().Select(routineTraining)
	_, err := s.DB.Model(routineTraining).WherePK().Delete()
	if err != nil {
		return nil, err
	}
	_, err = s.DB.Model((*RoutineTraining)(nil)).Exec(fmt.Sprintf(`
		UPDATE routine_training 
		SET "order" = "order" - 1
		WHERE "order" > %d AND id_routine = '%s'
	`, routineTraining.Order, routineTraining.RoutineID))
	if err != nil {
		return nil, err
	}

	msg := pb.Response{Msg: "Training removed"}
	return &msg, nil
}

func (s *RoutineApiServer) DeleteByParticipant(ctx context.Context, req *pb.ParticipantDeleted) (*pb.Response, error) {
	fmt.Println("Deleting routines")

	routine := &Routine{
		UserID: req.Id,
	}
	_, err := s.DB.Model(routine).Where("user_id = ?", routine.UserID).Delete()
	if err != nil {
		return nil, err
	}

	msg := pb.Response{Msg: "Routines deleted"}
	return &msg, nil
}
