package server

import (
	"context"
	"fmt"
	pb "summary-service/pkg/api/proto"
	"time"

	"github.com/go-pg/pg/v10"
	"github.com/google/uuid"
)

type SummaryHandler struct {
	pb.UnimplementedSummaryAPIServer
	DB *pg.DB
}

func (h *SummaryHandler) Save(ctx context.Context, req *pb.SummaryCreated) (*pb.Response, error) {
	fmt.Println("Saving summary")

	var errs []error
	id, err := uuid.Parse(req.Id)
	errs = append(errs, err)
	routineId, err := uuid.Parse(req.RoutineId)
	errs = append(errs, err)

	for _, err := range errs {
		if err != nil {
			return nil, err
		}
	}

	summary := &Summary{
		ID:      id,
		Routine: routineId,
	}

	_, err = h.DB.Model(summary).Insert()
	if err != nil {
		return nil, err
	}

	msg := pb.Response{Msg: "Summary saved"}
	return &msg, nil
}

func (h *SummaryHandler) Update(ctx context.Context, req *pb.SummaryUpdated) (*pb.Response, error) {
	fmt.Println("Updating summary")

	var errs []error
	id, err := uuid.Parse(req.Id)
	errs = append(errs, err)
	routineId, err := uuid.Parse(req.RoutineId)
	errs = append(errs, err)
	maxtime, err := time.ParseDuration(req.Maxtime)
	errs = append(errs, err)
	mintime, err := time.ParseDuration(req.Mintime)
	errs = append(errs, err)

	for _, err := range errs {
		if err != nil {
			return nil, err
		}
	}

	summary := &Summary{
		ID:      id,
		Routine: routineId,
		Maxtime: maxtime,
		Mintime: mintime,
	}

	_, err = h.DB.Model(summary).WherePK().Update()
	if err != nil {
		return nil, err
	}

	msg := pb.Response{Msg: "Summary updated"}
	return &msg, nil
}
