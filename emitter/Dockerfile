FROM 538721144237.dkr.ecr.eu-west-1.amazonaws.com/maven-local:jdk11 as builder
USER root
COPY . .
RUN mvn clean package -DskipTests=true -pl emitter

FROM openjdk:11.0.4-jdk
RUN apt-get update
RUN apt-get -y install dnsutils
RUN apt install -y telnet
ARG EMITTER_JAR_FILE
ENV TZ=Europe/Moscow
COPY --from=builder emitter/target/${EMITTER_JAR_FILE} app.jar
COPY --from=builder env .
ENTRYPOINT ["java","-jar", "app.jar"]