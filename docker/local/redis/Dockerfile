FROM redis:7.4

COPY /docker/local/api/docker-entrypoint.sh /
RUN chmod +x /docker-entrypoint.sh

COPY /docker/local/redis/redis.conf /usr/local/etc/redis/redis.conf

CMD ["redis-server", "/usr/local/etc/redis/redis.conf"]
