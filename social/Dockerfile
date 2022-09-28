FROM openjdk:17-bullseye
ENV SBT_VERSION 1.5.4
RUN curl -L -o sbt-${SBT_VERSION}.zip https://github.com/sbt/sbt/releases/download/v${SBT_VERSION}/sbt-${SBT_VERSION}.zip
RUN unzip sbt-$SBT_VERSION.zip -d ops

WORKDIR /workit-social-dimension-microservice/

COPY project/ /workit-social-dimension-microservice/project
COPY build.sbt /workit-social-dimension-microservice/build.sbt

RUN /ops/sbt/bin/sbt social/update


COPY certs /workit-social-dimension-microservice/certs
COPY social/ /workit-social-dimension-microservice/social
COPY social/src/main/resources/application.prod.conf /workit-social-dimension-microservice/social/src/main/resources/application.conf
COPY protobuf/ /workit-social-dimension-microservice/protobuf

RUN /ops/sbt/bin/sbt social/assembly
EXPOSE 5000
CMD [ "java", "-jar", "social/target/scala-2.13/workit.socialMicroservice.jar" ]