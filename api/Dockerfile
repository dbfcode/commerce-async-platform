FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

RUN apk add --no-cache maven

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
COPY dev-entrypoint.sh /app/dev-entrypoint.sh
# Normalize CRLF from Windows checkouts so the shebang works in Alpine
RUN sed -i 's/\r$//' /app/dev-entrypoint.sh && chmod +x /app/dev-entrypoint.sh

CMD ["/app/dev-entrypoint.sh"]