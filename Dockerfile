FROM maven:3.9.5-eclipse-temurin-11-alpine

RUN apk add wget && \
    wget https://github.com/JetBrains/kotlin/releases/download/v1.8.0/kotlin-compiler-1.8.0.zip && \
    unzip kotlin-compiler-1.8.0.zip -d /opt && \
    rm kotlin-compiler-1.8.0.zip

WORKDIR /app
COPY . /app
RUN mvn clean package