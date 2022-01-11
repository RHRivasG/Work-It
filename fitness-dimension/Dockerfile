FROM golang:1.17.6-alpine3.15

# Expose HTTP port and set necessary environment variables
EXPOSE 8080

# copy source code into the $GOPATH and switch to that directory
COPY . /work-it/fitness-dimension/
COPY .env.production /work-it/fitness-dimension/.env
WORKDIR /work-it/fitness-dimension/

# compile source code and copy into $PATH
RUN go build

# the default command runs the service in the foreground
CMD ["./fitness-dimension"]