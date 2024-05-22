FROM gradle:8.7.0-jdk17
COPY . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build
EXPOSE 8082
ENTRYPOINT ["java","-jar","/home/brosoft/IdeaProjects/rulesManager/build/libs/rulesManager-0.0.1-SNAPSHOT.jar"]