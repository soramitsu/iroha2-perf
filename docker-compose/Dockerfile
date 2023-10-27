FROM openjdk:11-jdk

RUN apt-get update && apt-get install -y wget unzip openjdk-11-jdk maven
RUN apt-get install -y wget && \
    wget https://github.com/JetBrains/kotlin/releases/download/v1.8.0/kotlin-compiler-1.8.0.zip && \
    unzip kotlin-compiler-1.8.0.zip -d /opt && \
    rm kotlin-compiler-1.8.0.zip

WORKDIR /app
COPY . /app
RUN mvn clean install

CMD ["java", "-jar", "iroha2-perf-1.0.jar"]