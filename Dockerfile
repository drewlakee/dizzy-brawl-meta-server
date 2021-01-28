FROM openjdk:14-alpine

LABEL maintainer = "Andrew Aleynikov [drew.lake@yandex.ru]"

COPY . /

RUN chmod 777 gradlew
RUN ./gradlew build --no-daemon

RUN cp /build/libs/app.jar /bin

ENTRYPOINT java -jar /bin/app.jar
