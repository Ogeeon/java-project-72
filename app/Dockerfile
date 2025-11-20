FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY gradlew .

RUN ./gradlew dependencies

COPY src src
COPY config config

EXPOSE 7070

RUN ./gradlew installDist
CMD ./build/install/app/bin/app
