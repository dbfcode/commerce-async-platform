FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

RUN apk add --no-cache maven

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src

CMD ["mvn", "spring-boot:run", \
     "-Dspring-boot.run.profiles=docker", \
     "-Dspring-boot.run.jvmArguments=-Dspring.devtools.restart.poll-interval=2000 -Dspring.devtools.restart.quiet-period=1000"]