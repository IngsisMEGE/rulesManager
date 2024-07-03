FROM gradle:8.7.0-jdk17

WORKDIR /home/gradle/src

COPY build.gradle settings.gradle gradle/ ./
COPY src ./src
COPY .editorconfig ./
ARG NEW_RELIC_LICENSE_KEY
ARG NEW_RELIC_APP_NAME

ENV NEW_RELIC_LICENSE_KEY=$NEW_RELIC_LICENSE_KEY
ENV NEW_RELIC_APP_NAME=$NEW_RELIC_APP_NAME

RUN gradle build

WORKDIR /app

EXPOSE ${PORT}

COPY newrelic/newrelic.jar /app/newrelic.jar

ENTRYPOINT ["java","-jar","-Dspring.profiles.active=production", "-javaagent:/app/newrelic.jar", "-Dnewrelic.config.license_key=${NEW_RELIC_LICENSE_KEY}","/home/gradle/src/build/libs/rulesManager-0.0.1-SNAPSHOT.jar"]