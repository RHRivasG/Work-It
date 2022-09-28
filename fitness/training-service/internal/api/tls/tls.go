package tls

import (
	"crypto/rand"
	"crypto/tls"
	"crypto/x509"
	"fmt"
	"io/ioutil"
	"log"
	"net"

	"golang.org/x/net/http2"
	"google.golang.org/grpc/credentials"
)

func GimmeTLS(tcpl net.Listener, cert, key string) (tlsl net.Listener) {
	certificate, err := tls.LoadX509KeyPair(cert, key)
	if err != nil {
		log.Fatal(err)
	}

	const requiredCipher = tls.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256
	config := &tls.Config{
		CipherSuites: []uint16{requiredCipher},
		NextProtos:   []string{http2.NextProtoTLS, "h2-14"}, // h2-14 is just for compatibility. will be eventually removed.
		Certificates: []tls.Certificate{certificate},
	}
	config.Rand = rand.Reader

	tlsl = tls.NewListener(tcpl, config)
	return
}

func LoadTLSCredentials() (credentials.TransportCredentials, error) {
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
