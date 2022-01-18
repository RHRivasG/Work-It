FROM openjdk:17-bullseye
ENV SBT_VERSION 1.5.4
RUN curl -L -o sbt-${SBT_VERSION}.zip https://github.com/sbt/sbt/releases/download/v${SBT_VERSION}/sbt-${SBT_VERSION}.zip
RUN unzip sbt-$SBT_VERSION.zip -d ops

WORKDIR /workit-service-aggregator-microservice/

COPY certs/ /workit-service-aggregator-microservice/certs

WORKDIR /workit-service-aggregator-microservice/work-it

COPY project/ /workit-service-aggregator-microservice/work-it/project
COPY build.sbt /workit-service-aggregator-microservice/work-it/build.sbt

RUN /ops/sbt/bin/sbt aggregator/update

COPY aggregator/ /workit-service-aggregator-microservice/work-it/aggregator
COPY protobuf/ /workit-service-aggregator-microservice/work-it/protobuf

RUN /ops/sbt/bin/sbt aggregator/assembly
EXPOSE 5000
CMD [ "java", "-jar", "aggregator/target/scala-2.13/workit.serviceAggregator.jar" ]