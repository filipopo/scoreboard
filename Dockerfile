FROM maven:3-eclipse-temurin-21-alpine AS base

WORKDIR /opt

COPY pom.xml .

COPY src ./src

RUN mvn package -DskipTests


FROM eclipse-temurin:21-jre-alpine

WORKDIR /opt

COPY --from=base /opt/target/scoreboard-1.0-SNAPSHOT.jar scoreboard.jar

COPY en.json korisno.json sr.json .

ENTRYPOINT ["java", "-jar", "scoreboard.jar", "--cli"]

CMD ["en.json"]
