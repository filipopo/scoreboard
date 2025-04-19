ARG JAVA_VER=21

FROM maven:3-eclipse-temurin-${JAVA_VER}-alpine AS base

WORKDIR /opt

COPY pom.xml .

COPY src ./src

RUN mvn package -DskipTests


FROM eclipse-temurin:${JAVA_VER}-jre-alpine

WORKDIR /opt

COPY --from=base /opt/target/scoreboard-1.0-SNAPSHOT.jar scoreboard.jar

COPY en.json korisno.json sr.json .

ENTRYPOINT ["java", "-jar", "scoreboard.jar", "--cli"]

CMD ["en.json"]
