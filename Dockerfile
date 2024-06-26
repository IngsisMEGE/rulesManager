FROM gradle:8.7.0-jdk17

WORKDIR /home/gradle/src

COPY build.gradle settings.gradle gradle/ ./
COPY src ./src
COPY .editorconfig ./

RUN gradle build

WORKDIR /app

EXPOSE ${PORT}

ENTRYPOINT ["java","-jar","/home/gradle/src/build/libs/rulesManager-0.0.1-SNAPSHOT.jar"]