FROM gradle:8.8-jdk21

WORKDIR /app

COPY . .

COPY /docker/development/api/docker-entrypoint.sh /
RUN chmod +x /docker-entrypoint.sh

CMD ["/docker-entrypoint.sh"]
