# AGENTS.md

## Cursor Cloud specific instructions

### Overview

OrderFlow Commerce is a monorepo with two services:

| Service | Path | Tech | Dev Port |
|---------|------|------|----------|
| API (backend) | `api/` | Spring Boot 3.2 / Java 21 / Maven | 8080 |
| Web (frontend) | `web/` | React 19 / Vite 8 / TypeScript | 5173 |

Infrastructure (PostgreSQL 15, RabbitMQ 3) runs via Docker Compose.

### Starting infrastructure

```bash
sudo dockerd &>/tmp/dockerd.log &   # if Docker daemon is not already running
sudo docker compose up -d postgres rabbitmq
```

Wait for both containers to become healthy before starting the API.

### Running the API locally

The default `application.properties` uses Docker Compose hostnames (`postgres`, `rabbitmq`). For local dev outside Docker, use the `local` Spring profile (which reads `application-local.properties` with `localhost` hostnames):

```bash
cd api && ./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

### Running the Web frontend

```bash
cd web && npm run dev
```

Vite proxies `/api` requests to `http://localhost:8080` (see `vite.config.ts`).

### Tests and Lint

- **API tests**: `cd api && ./mvnw test`
- **Web lint**: `cd web && npm run lint`
- **Web build**: `cd web && npm run build`

### Key endpoints when running

- API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui/index.html
- Web: http://localhost:5173
- RabbitMQ Management: http://localhost:15672 (user: `orderflow` / pass: `orderflow123`)

### Non-obvious notes

- Security is currently set to `permitAll()` — no auth required for any endpoint.
- The `mvnw` wrapper must be `chmod +x` before first use (it's committed without execute bit).
- Hibernate `ddl-auto=update` auto-creates schema on first run — no migration step needed.
- The `application-local.properties` file (added for Cloud dev) overrides DB/RabbitMQ hosts to `localhost`. If you need to run the API inside Docker Compose, use `-Dspring-boot.run.profiles=docker` instead.
- `spring-boot-devtools` is enabled: the API auto-restarts on class changes, but not on `pom.xml` changes.
