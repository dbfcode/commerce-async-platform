# Monorepo — frontend e API
<img width="1536" height="1024" alt="ChatGPT Image 30 de abr  de 2026, 13_52_47 (1)" src="https://github.com/user-attachments/assets/55231973-4991-4e35-95c3-aac612f0bcbc" />

Este repositório contém:

| Pasta | Descrição |
|-------|-----------|
| [`api/`](api/) | Spring Boot (OrderFlow Commerce) |
| [`web/`](web/) | Frontend Vite + React |

## Subir tudo com Docker

Na **raiz** do repositório:

```bash
docker compose up --build
```

- API: http://localhost:8080  
- Swagger: http://localhost:8080/swagger-ui.html  
- Web (nginx): http://localhost:4173  
- RabbitMQ UI: http://localhost:15672  

## Desenvolvimento local

- **API:** `cd api && ./mvnw spring-boot:run` (Windows: `mvnw.cmd`)  
- **Web:** `cd web && npm install && npm run dev`  

Detalhes da API, stack e variáveis: [`api/README.md`](api/README.md).
