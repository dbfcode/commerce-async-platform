#!/bin/sh
set -e

echo "[dev] Compiling project..."
mvn compile -q -B

echo "[dev] Starting file watcher on src/..."
(
  while inotifywait -r -q -e modify,create,delete,move \
        --include '\.java$|\.properties$|\.yml$|\.yaml$|\.xml$' \
        src/ 2>/dev/null; do
    echo "[dev] Source change detected — recompiling..."
    mvn compile -q -B 2>&1 || echo "[dev] Compilation failed (check logs above)."
  done
) &

echo "[dev] Starting Spring Boot..."
exec mvn spring-boot:run \
  -Dspring-boot.run.profiles=${SPRING_PROFILES_ACTIVE:-docker} \
  -Dspring-boot.run.jvmArguments="${SPRING_BOOT_JVM_ARGS:--agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005}"
