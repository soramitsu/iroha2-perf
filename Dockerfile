FROM maven:3.9.5-eclipse-temurin-11-alpine as builder

RUN wget https://github.com/JetBrains/kotlin/releases/download/v1.8.0/kotlin-compiler-1.8.0.zip && \
    unzip kotlin-compiler-1.8.0.zip -d /opt && \
    rm kotlin-compiler-1.8.0.zip

WORKDIR /opt/app
COPY pom.xml ./
COPY ./src ./src
RUN mvn clean package


FROM maven:3.9.5-eclipse-temurin-11-alpine

WORKDIR /opt/app
COPY --from=builder /opt/app/pom.xml ./
COPY --from=builder /opt/app/target/ /opt/app/target/

ENTRYPOINT ["/usr/local/bin/mvn-entrypoint.sh"]
