FROM openjdk:14-alpine

LABEL maintainer = "Andrew Aleynikov [drew.lake@yandex.ru]"

COPY . /

RUN ./gradlew build --no-daemon

ENTRYPOINT java -jar build/libs/db-server-1.0.jar
