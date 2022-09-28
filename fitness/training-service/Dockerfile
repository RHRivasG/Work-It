FROM golang:1.17.6-alpine3.15

# Expose HTTP port and set necessary environment variables
EXPOSE 8080

# copy source code into the $GOPATH and switch to that directory
COPY fitness/training-service /work-it/training-service/
COPY certs /certs/
COPY fitness/training-service/.env.production /work-it/training-service/.env
WORKDIR /work-it/training-service/

# compile source code and copy into $PATH
RUN go build -o training-service.exe ./cmd/server/main.go 

# the default command runs the service in the foreground
CMD ["./training-service.exe"]
