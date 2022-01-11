FROM java:8
ENV SBT_VERSION 1.5.4
RUN curl -L -o sbt-${SBT_VERSION}.zip https://github.com/sbt/sbt/releases/download/v${SBT_VERSION}/sbt-${SBT_VERSION}.zip
RUN unzip sbt-$SBT_VERSION.zip -d ops
COPY build.sbt /workit-social-dimension-microservice/build.sbt
COPY social/ /workit-social-dimension-microservice/social
COPY project/ /workit-social-dimension-microservice/project
COPY protobuf/ /workit-social-dimension-microservice/protobuf
WORKDIR /workit-social-dimension-microservice/

RUN /ops/sbt/bin/sbt social/assembly
EXPOSE 5000

CMD [ "java", "-jar", "social/target/scala-2.13/workit.socialMicroservice.jar" ]