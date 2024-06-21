FROM eclipse-temurin:21

ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} /app.jar

COPY /docker/api/docker-entrypoint.sh /
RUN chmod +x /docker-entrypoint.sh

CMD ["/docker-entrypoint.sh"]
