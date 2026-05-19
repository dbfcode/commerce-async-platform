FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

RUN apk add --no-cache maven inotify-tools

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
COPY dev-entrypoint.sh /app/dev-entrypoint.sh
RUN chmod +x /app/dev-entrypoint.sh

CMD ["/app/dev-entrypoint.sh"]