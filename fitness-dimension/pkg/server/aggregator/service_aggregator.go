package aggregator

import (
	"context"
	"fitness-dimension/pkg/env"
	"log"
	"os"

	pb "fitness-dimension/pkg/api/proto"

	"google.golang.org/grpc"
)

func SetServiceAggregator(sigChannel chan os.Signal) error {
	host := env.GoDotEnvVariable("SERVICE_AGGREGATOR")
	conn, err := grpc.Dial(host, grpc.WithInsecure())
	if err != nil {
		return err
	}
	client := pb.NewServiceAggregatorClient(conn)
	res, err := client.AddService(context.Background(), &pb.AddServiceMessage{
		Group:    "fitness",
		Capacity: 1,
	})
	if err != nil {
		return err
	}
	log.Println(res)

	defer client.Unsubscribe(context.Background(), &pb.UnsubscribeMessage{})
	defer conn.Close()
	go cleanUp(conn, client, sigChannel)

	return nil
}

func cleanUp(conn *grpc.ClientConn, client pb.ServiceAggregatorClient, sigChannel chan os.Signal) {
	select {
	case <-sigChannel:
		client.Unsubscribe(context.Background(), &pb.UnsubscribeMessage{})
		conn.Close()
		os.Exit(0)
	}
}
