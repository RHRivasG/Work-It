package aggregator

import (
	"context"
	"fitness-dimension/pkg/env"
	"log"
	"os"

	pb "fitness-dimension/pkg/api/proto"

	"google.golang.org/grpc"
)

func SetServiceAggregator() (*grpc.ClientConn, pb.ServiceAggregatorClient, error) {

	host := env.GoDotEnvVariable("SERVICE_AGGREGATOR")
	conn, err := grpc.Dial(host, grpc.WithInsecure())
	if err != nil {
		return nil, nil, err
	}

	client := pb.NewServiceAggregatorClient(conn)
	res, err := client.AddService(context.Background(), &pb.AddServiceMessage{
		Group:    "fitness",
		Capacity: 1,
	})
	if err != nil {
		return nil, nil, err
	}
	log.Println(res)

	return conn, client, nil
}

func CleanUp(client pb.ServiceAggregatorClient, conn *grpc.ClientConn, sigChannel chan os.Signal) {
	select {
	case <-sigChannel:
		client.Unsubscribe(context.Background(), &pb.UnsubscribeMessage{})
		conn.Close()
		os.Exit(0)
	}
}
