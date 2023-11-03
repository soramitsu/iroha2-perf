FROM maven:3.9.5-eclipse-temurin-11-alpine as builder

RUN wget https://github.com/JetBrains/kotlin/releases/download/v1.8.0/kotlin-compiler-1.8.0.zip && \
    unzip kotlin-compiler-1.8.0.zip -d /opt && \
    rm kotlin-compiler-1.8.0.zip

WORKDIR /app
COPY pom.xml ./
COPY ./src ./src
RUN  mvn clean package

FROM maven:3.9.5-eclipse-temurin-11-alpine

ENV  USER=iroha
ENV  UID=1000
ENV  GID=1000
ENV  MAVEN_CONFIG=/var/maven/.m2

RUN  set -ex && \
     addgroup -g $GID $USER && \
     adduser \
     --disabled-password \
     --gecos "" \
     --home /app \
     --ingroup "$USER" \
     --uid "$UID" \
     "$USER" && \
     mkdir -p $MAVEN_CONFIG && \
     chown $USER:$USER -R $MAVEN_CONFIG

WORKDIR /app
COPY --from=builder /app/pom.xml ./
COPY --from=builder /app/target/ ./target/

USER $USER

ENTRYPOINT ["/usr/local/bin/mvn-entrypoint.sh"]
