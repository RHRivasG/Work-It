package training

import (
	"context"
	pb "fitness-dimension/pkg/api/proto"
	"fmt"

	"github.com/go-pg/pg/v10"
)

type TrainingApiServer struct {
	pb.UnimplementedTrainingAPIServer
	DB *pg.DB
}

func (s *TrainingApiServer) Save(ctx context.Context, req *pb.TrainingCreated) (*pb.Response, error) {
	fmt.Println("Saving training")

	training := &Training{
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

	training := &Training{
		ID:          req.Id,
		TrainerID:   req.TrainerId,
		Name:        req.Name,
		Description: req.Description,
		Categories:  req.Categories,
	}
	_, err := s.DB.Model(training).WherePK().Update()
	if err != nil {
		return nil, err
	}

	msg := pb.Response{Msg: "Training updated"}
	return &msg, nil
}

func (s *TrainingApiServer) Delete(ctx context.Context, req *pb.TrainingDeleted) (*pb.Response, error) {
	fmt.Println("Deleting training")

	//TODO destroy video

	training := &Training{
		ID: req.Id,
	}
	_, err := s.DB.Model(training).WherePK().Delete()
	if err != nil {
		return nil, err
	}

	msg := pb.Response{Msg: "Training deleted"}
	return &msg, nil
}

func (s *TrainingApiServer) SaveVideo(ctx context.Context, req *pb.TrainingVideoCreated) (*pb.Response, error) {
	fmt.Println("Saving video")

	video := &TrainingVideo{
		ID:         req.Id,
		Name:       req.Name,
		Ext:        req.Ext,
		Buff:       []byte(req.Video),
		TrainingID: req.TrainingId,
	}

	var existsVideo TrainingVideo
	err := s.DB.Model().Table("videos").Where("training_id = ?", video.TrainingID).Select(&existsVideo)
	if err != nil && err != pg.ErrNoRows {
		return nil, err
	}
	if &existsVideo.ID != nil {
		_, err := s.DB.Model(&existsVideo).WherePK().Delete()
		if err != nil {
			return nil, err
		}
	}

	_, err = s.DB.Model(video).Insert()
	if err != nil {
		return nil, err
	}

	msg := pb.Response{Msg: "Video saved"}
	return &msg, nil
}

func (s *TrainingApiServer) DeleteVideo(ctx context.Context, req *pb.TrainingVideoDeleted) (*pb.Response, error) {
	fmt.Println("Deleting video")

	video := &TrainingVideo{
		ID: req.Id,
	}
	_, err := s.DB.Model(video).WherePK().Delete()
	if err != nil {
		return nil, err
	}

	msg := pb.Response{Msg: "Video deleted"}
	return &msg, nil
}

func (s *TrainingApiServer) DeleteByTrainer(ctx context.Context, req *pb.TrainerDeleted) (*pb.Response, error) {
	fmt.Println("Deleting trainings")

	training := &Training{
		TrainerID: req.Id,
	}

	_, err := s.DB.Model(training).Where("trainer_id = ?", &training.TrainerID).Delete()
	if err != nil {
		return nil, err
	}

	msg := pb.Response{Msg: "trainings deleted"}
	return &msg, nil

}
