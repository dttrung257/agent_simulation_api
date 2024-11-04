FROM redis:7.4

COPY /docker/development/redis/redis.conf /usr/local/etc/redis/redis.conf

COPY /docker/development/api/docker-entrypoint.sh /
RUN chmod +x /docker-entrypoint.sh

CMD ["/docker-entrypoint.sh"]
