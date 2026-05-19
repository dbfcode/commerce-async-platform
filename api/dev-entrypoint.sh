#!/bin/sh
set -e

# Snapshot mtimes of source files (polling works on Windows bind mounts; inotify often does not).
snapshot() {
  find src -type f \( \
    -name '*.java' -o -name '*.properties' -o -name '*.xml' -o \
    -name '*.yml' -o -name '*.yaml' \
  \) -exec stat -c '%Y %n' {} \; 2>/dev/null | LC_ALL=C sort | md5sum | awk '{print $1}'
}

compile_sources() {
  echo "[dev] Source change detected — recompiling..."
  mvn compile -q -B 2>&1 || echo "[dev] Compilation failed (check logs above)."
}

echo "[dev] Compiling project..."
mvn compile -q -B

echo "[dev] Starting source watcher (poll every 2s)..."
(
  last=$(snapshot)
  while true; do
    sleep 2
    current=$(snapshot)
    if [ "$current" != "$last" ]; then
      compile_sources
      last=$current
    fi
  done
) &

echo "[dev] Starting Spring Boot..."
exec mvn spring-boot:run \
  -Dspring-boot.run.profiles=${SPRING_PROFILES_ACTIVE:-docker} \
  -Dspring-boot.run.jvmArguments="${SPRING_BOOT_JVM_ARGS:--agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005}"
