FROM openjdk:8-alpine
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} tajniaki-server.jar
ADD ./resources ./resources
ENTRYPOINT ["java","-jar","/tajniaki-server.jar"]