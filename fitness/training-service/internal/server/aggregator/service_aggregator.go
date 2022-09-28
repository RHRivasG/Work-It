package aggregator

import (
	"context"
	"crypto/tls"
	"crypto/x509"
	"fmt"
	"io/ioutil"
	"log"
	"os"
	"training-service/internal/env"

	pb "training-service/internal/api/proto"

	"google.golang.org/grpc"
	"google.golang.org/grpc/credentials"
)

func SetServiceAggregator() (*grpc.ClientConn, pb.ServiceAggregatorClient, error) {

	//TLS
	tlsCredentials, err := loadTLSCredentials()
	if err != nil {
		return nil, nil, err
	}

	host := env.GoDotEnvVariable("SERVICE_AGGREGATOR")
	conn, err := grpc.Dial(host, grpc.WithTransportCredentials(tlsCredentials))
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

func loadTLSCredentials() (credentials.TransportCredentials, error) {
	// Load certificate of the CA who signed server's certificate
	pemServerCA, err := ioutil.ReadFile("../../certs/ca/cert.pem")
	if err != nil {
		return nil, err
	}

	certPool := x509.NewCertPool()
	if !certPool.AppendCertsFromPEM(pemServerCA) {
		return nil, fmt.Errorf("failed to add server CA's certificate")
	}

	// Create the credentials and return it
	config := &tls.Config{
		RootCAs: certPool,
	}

	return credentials.NewTLS(config), nil
}
