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
		TrainerID:   req.TraineId,
		Name:        req.Name,
		Description: req.Description,
		VideoID:     req.VideoId,
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
	fmt.Println("Saving training")

	training := &models.Training{}
	_, err := s.DB.Model(training).Insert()
	if err != nil {
		return nil, err
	}

	msg := pb.Response{Msg: "Training saved"}
	return &msg, nil
}

func (s *TrainingApiServer) UpdateVideo(ctx context.Context, req *pb.TrainingVideoUpdated) (*pb.Response, error) {
	fmt.Println("Updating training video")

	training := &models.Training{}
	_, err := s.DB.Model(training).WherePK().Update()
	if err != nil {
		return nil, err
	}

	msg := pb.Response{Msg: "Training updated"}
	return &msg, nil
}

func (s *TrainingApiServer) DeleteVideo(ctx context.Context, req *pb.TrainingVideoDeleted) (*pb.Response, error) {
	fmt.Println("Deleting training video")

	training := &models.Training{}
	_, err := s.DB.Model(training).WherePK().Delete()
	if err != nil {
		return nil, err
	}

	msg := pb.Response{Msg: "Training video deleted"}
	return &msg, nil
}
