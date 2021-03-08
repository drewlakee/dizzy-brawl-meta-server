FROM openjdk:14-alpine

LABEL maintainer = "Andrew Aleynikov [drew.lake@yandex.ru]"

WORKDIR /dizzy-brawl-meta-server

COPY . /dizzy-brawl-meta-server

RUN chmod 777 gradlew
RUN ./gradlew build --no-daemon

ENTRYPOINT java -jar build/libs/dizzy-brawl-meta-server-1.0.0.jar
