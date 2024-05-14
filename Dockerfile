FROM ubuntu:latest AS build

RUN apt-get update
RUN apt-get install openjdk-21-jdk -y
COPY . .

RUN ./gradlew bootJar --no-daemon

FROM openjdk:21-jdk-slim

EXPOSE 8080

COPY --from=build /build/libs/ttt-1.jar ttt.jar

ENTRYPOINT ["java", "-jar", "ttt.jar"]