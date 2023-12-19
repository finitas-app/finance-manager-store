FROM amazoncorretto:17-alpine-jdk

VOLUME /tmp
COPY ./build/libs/*.jar app.jar

ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-jar", "/app.jar"]
