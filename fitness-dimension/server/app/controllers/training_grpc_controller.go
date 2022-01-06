package controllers

import (
	"context"
	pb "fitness-dimension/gen/proto"
	"fitness-dimension/server/app/models"
	"fmt"

	"github.com/go-pg/pg/v10"
)

type TrainingApiServer struct {
	pb.UnimplementedTrainingAPIServer
	DB *pg.DB
}

func (s *TrainingApiServer) Save(ctx context.Context, req *pb.TrainingCreated) (*pb.Response, error) {
	fmt.Println("Saving training")

	training := &models.Training{
		ID:          req.Id,
		TrainerID:   req.TrainerId,
		Name:        req.Name,
		Description: req.Description,
		Categories:  req.Categories,
	}
	_, err := s.DB.Model(training).Insert()
	if err != nil {
		return nil, err
	}

	msg := pb.Response{Msg: "Training saved"}
	return &msg, nil
}

func (s *TrainingApiServer) Update(ctx context.Context, req *pb.TrainingUpdated) (*pb.Response, error) {
	fmt.Println("Updating training")

	training := &models.Training{}
	_, err := s.DB.Model(training).WherePK().Update()
	if err != nil {
		return nil, err
	}

	msg := pb.Response{Msg: "Training updated"}
	return &msg, nil
}

func (s *TrainingApiServer) Delete(ctx context.Context, req *pb.TrainingDeleted) (*pb.Response, error) {
	fmt.Println("Deleting training")

	//TODO destroy video and taxonomies

	training := &models.Training{}
	_, err := s.DB.Model(training).WherePK().Delete()
	if err != nil {
		return nil, err
	}

	msg := pb.Response{Msg: "Training deleted"}
	return &msg, nil
}

func (s *TrainingApiServer) SaveVideo(ctx context.Context, req *pb.TrainingVideoCreated) (*pb.Response, error) {
	fmt.Println("Saving video")
	fmt.Println(req)

	video := &models.TrainingVideo{
		ID:         req.Id,
		Name:       req.Name,
		Ext:        req.Ext,
		Buff:       []byte(req.Video),
		TrainingID: req.TrainingId,
	}
	_, err := s.DB.Model(video).Insert()
	if err != nil {
		return nil, err
	}

	msg := pb.Response{Msg: "Video saved"}
	return &msg, nil
}

func (s *TrainingApiServer) DeleteVideo(ctx context.Context, req *pb.TrainingVideoDeleted) (*pb.Response, error) {
	fmt.Println("Deleting video")

	training := &models.Training{}
	_, err := s.DB.Model(training).WherePK().Delete()
	if err != nil {
		return nil, err
	}

	msg := pb.Response{Msg: "Video deleted"}
	return &msg, nil
}
