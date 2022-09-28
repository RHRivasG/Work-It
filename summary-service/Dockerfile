FROM golang:1.17.6-alpine3.15

# Expose HTTP port and set necessary environment variables
EXPOSE 8080

# copy source code into the $GOPATH and switch to that directory
COPY summary-service /work-it/summary-service/
COPY summary-service/.env.production /work-it/summary-service/.env
WORKDIR /work-it/summary-service/

# compile source code and copy into $PATH
RUN go build -o summary_service.exe ./cmd/summary_service/main.go 

# the default command runs the service in the foreground
CMD ["./summary_service.exe"]