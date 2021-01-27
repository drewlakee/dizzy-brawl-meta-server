FROM openjdk:14-alpine

LABEL maintainer = "Andrew Aleynikov [drew.lake@yandex.ru]"

COPY . /

RUN ./gradlew build --no-daemon

COPY build/libs/app.jar /bin

ENTRYPOINT java -jar /bin/app.jar
