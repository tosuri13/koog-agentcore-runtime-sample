# Stage 1: Cache Gradle dependencies
FROM gradle:8.14.3 AS cache

RUN mkdir -p /home/gradle/cache_home
ENV GRADLE_USER_HOME=/home/gradle/cache_home

COPY build.gradle.* settings.gradle.* gradle.properties /home/gradle/app/
COPY gradle /home/gradle/app/gradle

WORKDIR /home/gradle/app
RUN gradle clean build -i --stacktrace

# Stage 2: Build Application
FROM gradle:8.14.3 AS build

COPY --from=cache /home/gradle/cache_home /home/gradle/.gradle
COPY --chown=gradle:gradle . /home/gradle/src

WORKDIR /home/gradle/src
RUN gradle buildFatJar --no-daemon

# Stage 3: Create the Runtime Image
FROM eclipse-temurin:21-jre AS runtime
EXPOSE 8080

RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/*.jar /app/main.jar

ENTRYPOINT ["java","-jar","/app/main.jar"]