# Work It 🏋

Work It is a web application that will help you stay fit by doing the exercises you find in it and creating your own routines. In the process you have the possibility to become a trainer to share your routines with others!

## ✨ Features

- Specify your interests
- Search trainings
- Create and play routines
- Get a summary of your progress

### If you are a trainer

- Share your own routines

## 🛠️ Getting Started

\*You need to have docker installed

```shell
docker-compose up -f  proxy.yml
```

```shell
docker-compose up -f  aggregator.yml
```

```shell
docker-compose up -f  fitness-dimension.yml
```

```shell
docker-compose up -f  social-dimension.yml
```

## 📐 Archquitecture

### Context

![Diagramas-Contexto drawio](https://user-images.githubusercontent.com/51801113/170876390-28c128ad-e4ed-4384-becc-1bb92fbd60c7.png)

### Containers

![Diagramas-Contenedores drawio](https://user-images.githubusercontent.com/51801113/170878342-31a1f23e-815b-41fd-bec1-c3ddb7842320.png)

### Components

#### Proxy

![Diagramas-Componentes Proxy drawio](https://user-images.githubusercontent.com/51801113/170883260-a4f087dc-0753-4b75-82f6-a43555d461b9.png)

#### Service Agreggator

![Diagramas-Componentes Service Aggregator drawio](https://user-images.githubusercontent.com/51801113/170883502-19f4f391-f336-473a-9c3e-3b724fd192ec.png)

#### Social API

![Diagramas-Componentes Social API drawio](https://user-images.githubusercontent.com/51801113/170884795-b6fb4e24-8478-424b-a1d4-baf8b47071e7.png)

#### Fitness API

![Diagramas-Componentes Fitness API drawio](https://user-images.githubusercontent.com/51801113/170885725-637a31ce-5db2-4709-b896-474e2ca38ac8.png)
