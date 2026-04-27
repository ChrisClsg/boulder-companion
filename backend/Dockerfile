# ---------- Dev stage ----------
FROM maven:3.9.15-eclipse-temurin-25 AS dev

WORKDIR /build

COPY pom.xml .

RUN mvn -B dependency:go-offline -q

COPY src ./src

EXPOSE 8080
EXPOSE 8081

CMD ["mvn", "spring-boot:run"]


# ---------- Build stage ----------
FROM maven:3.9.15-eclipse-temurin-25 AS build

WORKDIR /build

COPY pom.xml .

RUN mvn -B dependency:go-offline -q

COPY src ./src

RUN mvn -B clean package -DskipTests


# ---------- Production runtime stage ----------
FROM eclipse-temurin:25-jre AS prod

WORKDIR /app

COPY --from=build /build/target/*.jar app.jar

EXPOSE 8080

ENV JAVA_OPTS=""

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
