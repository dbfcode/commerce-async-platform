FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

RUN apk add --no-cache maven inotify-tools

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
COPY dev-entrypoint.sh .
RUN chmod +x dev-entrypoint.sh

CMD ["./dev-entrypoint.sh"]