FROM openjdk:8-jre-alpine
ARG JAR_FILE
ARG JAR_CONFIG
COPY target/${JAR_CONFIG} config.properties
COPY target/${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar", "app.jar"]