FROM eclipse-temurin:26-noble AS builder

WORKDIR /src

COPY .mvn .mvn
COPY src src
COPY pom.xml .
COPY mvnw .

RUN chmod a+x mvnw && ./mvnw clean package -Dmaven.skip.test=true

FROM eclipse-temurin:26-jre-noble 

WORKDIR /app

COPY --from=builder /src/target/mymcpapp-0.0.1-SNAPSHOT.jar app.jar

ENV APP_PORT=3000

SHELL [ "/bin/sh", "-c" ]

ENTRYPOINT java -jar ./app.jar

