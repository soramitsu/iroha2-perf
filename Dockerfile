# change based image
FROM maven:3.8.4-openjdk-17-slim

WORKDIR /app

COPY pom.xml ./
# set bot ENV
COPY settings.xml /root/.m2/settings.xml
COPY performance-generator/pom.xml ./performance-generator/
COPY performance-generator/mvnw ./mvnw
COPY performance-generator/.mvn ./.mvn
COPY performance-generator ./performance-generator
#  модуль не критичен для работы нагрузочных тестов  util/pom.xml зависим от pom.xml. можно не копировать
COPY util ./util

RUN chmod +x mvnw
RUN cd /app/performance-generator
RUN mvn clean package -DskipTests
