FROM openjdk:17-bullseye
ENV SBT_VERSION 1.5.4
RUN curl -L -o sbt-${SBT_VERSION}.zip https://github.com/sbt/sbt/releases/download/v${SBT_VERSION}/sbt-${SBT_VERSION}.zip
RUN unzip sbt-$SBT_VERSION.zip -d ops

WORKDIR /workit-service-aggregator-microservice/
WORKDIR /workit-service-aggregator-microservice

COPY project/ /workit-service-aggregator-microservice/project
COPY build.sbt /workit-service-aggregator-microservice/build.sbt

RUN /ops/sbt/bin/sbt aggregator/update

COPY certs/ /workit-service-aggregator-microservice/certs
COPY aggregator/ /workit-service-aggregator-microservice/aggregator
COPY aggregator/src/main/resources/application.prod.conf /workit-service-aggregator-microservice/aggregator/src/main/resources/application.conf
COPY protobuf/ /workit-service-aggregator-microservice/protobuf

RUN /ops/sbt/bin/sbt aggregator/assembly
EXPOSE 5000
CMD [ "java", "-jar", "aggregator/target/scala-3.1.0/workit.serviceAggregator.jar" ]
