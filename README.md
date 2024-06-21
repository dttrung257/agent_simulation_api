# Agent Simulation API
## Setup environment
### Install Java 21 or later
Since installing Gradle takes a long time in the Docker container, we need to build the project first before running docker-compose.
#### Linux distro (Ubuntu/Debian)
```
sudo su

apt update
apt install wget
wget https://download.oracle.com/java/21/latest/jdk-21_linux-x64_bin.deb
sudo apt install ./jdk-21_linux-x64_bin.deb
rm -rf ./jdk-21_linux-x64_bin.deb
java --version
exit
```

### MacOS
```
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
brew install openjdk@21
```

## After clone
```
access project

cd src/main/resources
cp application.yml.example application.yml
cp application-dev.yml.example application-dev.yml
cp application-local.yml.example application-local.yml
```
Config base on your environment

## Run manually
```
./gradew build
java -jar build/libs/agent-simulation-api-0.0.1-SNAPSHOT.jar
```

## Run with docker
```
./gradew build
docker compose build
docker compose up -d (docker compose up)
```

## Log
See at storage/logs/spring.log
