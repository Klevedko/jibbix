FROM 538721144237.dkr.ecr.eu-west-1.amazonaws.com/maven-local:jdk11 as builder
USER root
COPY . .
RUN mvn clean package -DskipTests=true -pl jibbix_Inovus

FROM openjdk:11.0.4-jdk
RUN apt-get update
RUN apt-get -y install dnsutils
RUN apt install -y telnet
ARG JIBBIX_INOVUS_JAR_FILE
ENV TZ=Europe/Moscow
COPY --from=builder jibbix_Inovus/target/${JIBBIX_INOVUS_JAR_FILE} app.jar
COPY --from=builder env .
ENTRYPOINT ["java","-jar", "app.jar"]