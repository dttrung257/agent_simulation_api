#!/bin/bash

# Build docker and start database container
docker compose build
docker compose up db -d

# Add host.docker.internal to /etc/hosts to allow the container to access the host
if ! sudo grep -q "0.0.0.0 host.docker.internal" /etc/hosts; then
  sudo echo "0.0.0.0 host.docker.internal" | sudo tee -a /etc/hosts > /dev/null
fi

# Build api
./gradlew build

# Start the project
docker compose up -d
