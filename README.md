# OrderFlow Commerce

<p align="center">
  <img width="1536" height="1024" alt="OrderFlow Commerce" src="https://github.com/user-attachments/assets/55231973-4991-4e35-95c3-aac612f0bcbc" />
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk&logoColor=white" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.2-6DB33F?logo=springboot&logoColor=white" />
  <img src="https://img.shields.io/badge/PostgreSQL-15-4169E1?logo=postgresql&logoColor=white" />
  <img src="https://img.shields.io/badge/RabbitMQ-3-FF6600?logo=rabbitmq&logoColor=white" />
  <img src="https://img.shields.io/badge/React-19-61DAFB?logo=react&logoColor=black" />
  <img src="https://img.shields.io/badge/TypeScript-5-3178C6?logo=typescript&logoColor=white" />
  <img src="https://img.shields.io/badge/Docker-Compose-2496ED?logo=docker&logoColor=white" />
</p>

<p align="center">
  <b>Event-driven e-commerce platform</b> — plataforma de e-commerce orientada a eventos<br/>
  <i>Built with Java 21, Spring Boot, RabbitMQ, and React</i>
</p>

---

## What is OrderFlow? · O que é o OrderFlow?

OrderFlow Commerce is an **event-driven e-commerce REST API** that processes orders asynchronously.
When a customer checks out, an `OrderCreated` event is published to RabbitMQ — inventory reservation and email notifications happen **in the background**, without blocking the client.

O OrderFlow demonstra uma arquitetura **moderna e escalável**: mensageria assíncrona, API RESTful documentada com Swagger, e um monorepo com frontend React + backend Spring Boot, tudo orquestrado via Docker Compose com **hot reload** para desenvolvimento.

---

## Architecture · Arquitetura

> Diagramas seguem o [**C4 Model**](https://c4model.com/) — do contexto geral até o código.

### C1 — System Context · Contexto do Sistema

*Who interacts with the system? What are the external dependencies?*

```mermaid
graph TB
    subgraph External
        USER["🧑 User / Cliente"]
        PG["🐘 PostgreSQL 15<br/><i>Relational Database</i>"]
        RMQ["🐇 RabbitMQ 3<br/><i>Message Broker</i>"]
    end

    subgraph OrderFlow["⚡ OrderFlow Commerce"]
        API["Spring Boot API<br/><i>REST + Event Publishing</i>"]
        WEB["React SPA<br/><i>Vite + TypeScript + Tailwind</i>"]
    end

    USER -- "HTTP / Browser" --> WEB
    WEB -- "REST /api/*" --> API
    API -- "JDBC" --> PG
    API -- "AMQP" --> RMQ
    RMQ -- "consume events" --> API

    style OrderFlow fill:#1a1a2e,stroke:#16213e,color:#eee
    style API fill:#6DB33F,stroke:#4a8a2a,color:#fff
    style WEB fill:#61DAFB,stroke:#3a8fb7,color:#000
    style PG fill:#4169E1,stroke:#2a4494,color:#fff
    style RMQ fill:#FF6600,stroke:#cc5200,color:#fff
    style USER fill:#f5f5f5,stroke:#999,color:#333
```

### C2 — Containers · Contêineres

*What applications/services run? How do they communicate?*

```mermaid
graph LR
    subgraph Docker Compose
        direction TB

        PG[("🐘 PostgreSQL 15<br/>Port 5432<br/><code>orderflow</code> DB")]
        RMQ["🐇 RabbitMQ 3<br/>AMQP 5672 · UI 15672<br/>Exchange: <code>order.events</code>"]

        subgraph API["⚡ orderflow-app · Port 8080"]
            direction TB
            REST["REST Controllers<br/>/categories · /products · /test"]
            PUB["OrderEventPublisher<br/><i>Publishes OrderCreated</i>"]
            INV["InventoryConsumer<br/><i>Queue: order.inventory</i>"]
            EMAIL["EmailConsumer<br/><i>Queue: order.email</i>"]
        end

        subgraph WEB["🌐 orderflow-web · Port 5173"]
            VITE["Vite Dev Server<br/>React 19 + Tailwind 4"]
        end
    end

    VITE -- "/api/* proxy" --> REST
    REST -- "JPA / Hibernate" --> PG
    PUB -- "AMQP publish<br/>routing key: order.created" --> RMQ
    RMQ -- "consume" --> INV
    RMQ -- "consume" --> EMAIL

    style PG fill:#4169E1,stroke:#2a4494,color:#fff
    style RMQ fill:#FF6600,stroke:#cc5200,color:#fff
    style API fill:#1b4332,stroke:#2d6a4f,color:#eee
    style WEB fill:#0d1b2a,stroke:#1b3a5c,color:#eee
    style REST fill:#6DB33F,stroke:#4a8a2a,color:#fff
    style PUB fill:#81b29a,stroke:#588b76,color:#000
    style INV fill:#e07a5f,stroke:#c25a3f,color:#fff
    style EMAIL fill:#e07a5f,stroke:#c25a3f,color:#fff
    style VITE fill:#61DAFB,stroke:#3a8fb7,color:#000
```

### C3 — Components · Componentes da API

*Internal modules of the Spring Boot application.*

```mermaid
graph TB
    subgraph Controllers["🎯 Controllers (REST)"]
        CC["CategoryController<br/>/categories"]
        PC["ProductController<br/>/products"]
        TC["TestController<br/>/test"]
    end

    subgraph Messaging["📨 Messaging (RabbitMQ)"]
        OEP["OrderEventPublisher"]
        IC["InventoryConsumer"]
        EC["EmailConsumer"]
        RMC["RabbitMQConfig<br/><i>Exchange + Queues + Bindings</i>"]
    end

    subgraph Data["💾 Data Layer"]
        CR["CategoryRepository"]
        PR["ProductRepository"]
        OR["OrderRepository"]
        OIR["OrderItemRepository"]
    end

    subgraph Entities["📦 Domain Entities"]
        CAT["Category"]
        PROD["Product"]
        ORD["Order"]
        OI["OrderItem"]
    end

    subgraph Config["⚙️ Cross-Cutting"]
        SC["SecurityConfig<br/><i>CSRF off · Stateless · permitAll</i>"]
        OAC["OpenApiConfig<br/><i>Swagger UI</i>"]
        GEH["GlobalExceptionHandler<br/><i>400 · 404 · 500</i>"]
    end

    CC --> CR --> CAT
    PC --> PR --> PROD
    TC --> OEP
    OEP --> RMC
    CR --> CAT
    PR --> PROD
    OR --> ORD
    OIR --> OI
    PROD -.->|"@ManyToOne"| CAT
    OI -.->|"@ManyToOne"| ORD
    OI -.->|"@ManyToOne"| PROD

    style Controllers fill:#6DB33F,stroke:#4a8a2a,color:#fff
    style Messaging fill:#FF6600,stroke:#cc5200,color:#fff
    style Data fill:#4169E1,stroke:#2a4494,color:#fff
    style Entities fill:#2d6a4f,stroke:#1b4332,color:#fff
    style Config fill:#555,stroke:#333,color:#eee
```

### C4 — Entity Relationship · Modelo de Dados

```mermaid
erDiagram
    tb_category {
        bigint id PK "IDENTITY"
        varchar name UK "NOT NULL"
    }

    tb_product {
        bigint id PK "IDENTITY"
        varchar name "NOT NULL"
        text description
        decimal price "NOT NULL"
        int stock_quantity
        bigint category_id FK
    }

    tb_order {
        bigint id PK "IDENTITY"
        timestamp created_at "auto @PrePersist"
        varchar status "PENDING | CONFIRMED | SHIPPED | DELIVERED | CANCELLED"
        decimal total "sum(items)"
    }

    tb_order_item {
        bigint id PK "IDENTITY"
        int quantity
        decimal unit_price "snapshot do preco"
        bigint order_id FK "NOT NULL"
        bigint product_id FK "NOT NULL"
    }

    tb_category ||--o{ tb_product : "has many"
    tb_product ||--o{ tb_order_item : "referenced by"
    tb_order ||--o{ tb_order_item : "contains"
```

---

## Event Flow · Fluxo de Eventos

```mermaid
sequenceDiagram
    actor Client
    participant API as Spring Boot API
    participant PG as PostgreSQL
    participant RMQ as RabbitMQ
    participant INV as InventoryConsumer
    participant MAIL as EmailConsumer

    Client->>API: GET /test/publish-sample-order
    API->>RMQ: publish OrderCreatedEvent<br/>exchange: order.events<br/>routing key: order.created

    par Async Processing
        RMQ->>INV: queue: order.inventory
        INV->>INV: Reserve stock (simulated)
    and
        RMQ->>MAIL: queue: order.email
        MAIL->>MAIL: Send confirmation (simulated)
    end

    API-->>Client: 200 { published: true, orderId: 1000 }
```

---

## REST API Endpoints

| Method | Endpoint | Description | Request Body |
|--------|----------|-------------|--------------|
| `GET` | `/categories` | List all categories | — |
| `GET` | `/categories/{id}` | Get category by ID | — |
| `POST` | `/categories` | Create category | `{ "name": "..." }` |
| `PUT` | `/categories/{id}` | Update category | `{ "name": "..." }` |
| `DELETE` | `/categories/{id}` | Delete category | — |
| `GET` | `/products` | List all products | — |
| `GET` | `/products/{id}` | Get product by ID | — |
| `POST` | `/products` | Create product | `{ "name", "description", "price", "stockQuantity", "category": {"id": n} }` |
| `PUT` | `/products/{id}` | Update product | same as create |
| `DELETE` | `/products/{id}` | Delete product | — |
| `GET` | `/test/ping` | Health check | — |
| `GET` | `/test/publish-sample-order` | Publish test event to RabbitMQ | — |

> 📖 **Interactive docs:** [Swagger UI](http://localhost:8080/swagger-ui/index.html) (after starting the API)

---

## Tech Stack

| Layer | Technology | Purpose |
|-------|-----------|---------|
| **Language** | Java 21 | Backend runtime |
| **Framework** | Spring Boot 3.2 | REST API, DI, auto-config |
| **Database** | PostgreSQL 15 | Persistent storage |
| **ORM** | Spring Data JPA / Hibernate 6 | Object-relational mapping |
| **Messaging** | RabbitMQ 3 | Async event processing (AMQP) |
| **Security** | Spring Security | Auth framework (currently `permitAll`) |
| **API Docs** | SpringDoc OpenAPI 2.5 | Swagger UI generation |
| **Frontend** | React 19 + TypeScript | Single Page Application |
| **Styling** | Tailwind CSS 4 | Utility-first CSS |
| **Build (FE)** | Vite 8 | Dev server + bundler with HMR |
| **Build (BE)** | Maven (wrapper) | Dependency management + build |
| **DevOps** | Docker Compose | Local orchestration |
| **Testing** | JUnit 5 | Unit tests |
| **Dev Tools** | spring-boot-devtools + inotifywait | Hot reload in Docker |

---

## Quick Start · Como Rodar

### Prerequisites · Pré-requisitos

- **Docker Engine 24+** & **Docker Compose**
- **Git**

### Option 1: Docker Compose (recommended · recomendado)

```bash
git clone https://github.com/dbfcode/commerce-async-platform.git
cd commerce-async-platform

docker compose up --build
```

| Service | URL | Credentials |
|---------|-----|-------------|
| **API** | http://localhost:8080 | — |
| **Swagger UI** | http://localhost:8080/swagger-ui/index.html | — |
| **Web (Vite)** | http://localhost:5173 | — |
| **RabbitMQ UI** | http://localhost:15672 | `orderflow` / `orderflow123` |
| **PostgreSQL** | `localhost:5432` | `orderflow` / `orderflow123` / db: `orderflow` |
| **Debug (JDWP)** | `localhost:5005` | — |

> ♻️ **Hot reload** is enabled for both API (auto-recompile + DevTools restart) and Web (Vite HMR).

### Option 2: Local Development · Desenvolvimento Local

Start only infrastructure via Docker, run API and Web natively:

```bash
# Infrastructure
docker compose up -d postgres rabbitmq

# API (terminal 1)
cd api && ./mvnw spring-boot:run -Dspring-boot.run.profiles=local

# Web (terminal 2)
cd web && npm install && npm run dev
```

### Stopping · Parando

```bash
docker compose down
```

---

## Project Structure · Estrutura do Projeto

```
commerce-async-platform/
├── docker-compose.yml          # Orchestrates all services
├── AGENTS.md                   # Dev environment instructions
│
├── api/                        # ⚡ Spring Boot Backend
│   ├── Dockerfile              # Dev image (JDK + Maven + inotify)
│   ├── dev-entrypoint.sh       # File watcher for hot reload
│   ├── pom.xml                 # Maven dependencies
│   └── src/
│       ├── main/java/com/orderflow/ecommerce/
│       │   ├── Application.java
│       │   ├── config/         # Security, OpenAPI, RabbitMQ
│       │   ├── controllers/    # REST endpoints
│       │   ├── dtos/           # Data transfer objects
│       │   ├── entities/       # JPA entities + enums
│       │   ├── exceptions/     # Global error handler
│       │   ├── messaging/      # Publisher, consumers, events
│       │   └── repositories/   # Spring Data JPA interfaces
│       └── main/resources/
│           ├── application.properties
│           ├── application-docker.properties
│           └── application-local.properties
│
└── web/                        # 🌐 React Frontend
    ├── Dockerfile              # Production build (nginx)
    ├── Dockerfile.dev          # Development (Vite HMR)
    ├── package.json
    ├── vite.config.ts          # Proxy /api → backend
    └── src/
        ├── App.tsx             # Main component
        ├── lib/api.ts          # API client
        └── index.css           # Tailwind imports
```

---

## RabbitMQ Topology · Topologia de Mensageria

| Resource | Name | Type | Notes |
|----------|------|------|-------|
| **Exchange** | `order.events` | Topic (durable) | Central event hub |
| **Queue** | `order.inventory` | Durable | Inventory reservation |
| **Queue** | `order.email` | Durable | Email notifications |
| **Routing Key** | `order.created` | — | Binds both queues |

Both queues receive the same `OrderCreatedEvent` (fan-out via shared routing key), enabling **independent, parallel processing**.

---

## Roadmap · Evolução Planejada

> Detalhes em [`api/docs/microservices-migration.md`](api/docs/microservices-migration.md)

| Phase | Description | Status |
|-------|-------------|--------|
| 1 | **Baseline** — Monolith with event-driven order processing | ✅ Done |
| 2 | **Contracts** — Shared event schemas (`orderflow-contracts`) | 🔜 Planned |
| 3 | **Extract Workers** — Inventory & notification as separate services | 🔜 Planned |
| 4 | **Extract APIs** — Catalog & orders as independent microservices | 🔜 Planned |
| 5 | **Hardening** — DLQ, idempotency, observability, CI pipeline | 🔜 Planned |

**Planned integrations:** Redis caching (cart), JWT authentication, Resilience4j circuit breakers.

---

## Tests · Testes

```bash
# Backend unit tests
cd api && ./mvnw test

# Frontend lint
cd web && npm run lint

# Frontend build check
cd web && npm run build
```

---

## Contribution Workflow · Como contribuir

Para manter o monorepo organizado e o histórico do Git limpo, adotamos padrões estritos para a nomenclatura de **branches** e **mensagens de commit** (baseado em Conventional Commits).

---

### Branch Naming · Padrão de Branches

Toda nova alteração deve partir da branch principal utilizando a seguinte estrutura em **inglês** e com letras **minúsculas**:

#### Formato
```
padrão: [tipo-abreviado]/[escopo-opcional]-[breve-descrição]
```

#### Tipos Permitidos (Prefixos)
* `feat/` : Nova funcionalidade (ex: `feat/cart-page`)
* `fix/` : Correção de bug (ex: `fix/rabbitmq-retry`)
* `docs/` : Alterações exclusivas de documentação (ex: `docs/separate-swagger-docs`)
* `refactor/` : Refatoração de código que não altera o comportamento (ex: `refactor/clean-controllers`)
* `chore/` : Atualizações de build, dependências ou ferramentas (ex: `chore/update-docker-compose`)

---

### Semantic Commits · Commits Semânticos

As mensagens de commit devem ser escritas obrigatoriamente em **inglês**, utilizando letras **minúsculas** e o verbo no **imperativo** (ex: *add*, *fix*, *remove*, em vez de *added*, *fixed*, *removing*).

#### Formato
```
padrão: [tipo-abreviado](escopo): <descrição-curta>
```

#### Tabela de Tipos e Escopos

| Tipo | Uso | Escopo | Significado |
|:-----|:----| :--- | :--- |
| **feat**     | Nova funcionalidade | **auth** | Autenticação, JWT |
| **fix**      | Correção de bug | **produto** | CRUD de produtos |
| **docs**     | Documentação | **categoria** | CRUD de categorias |
| **style**    | Formatação, espaços, lint (não altera código) | **usuario** | CRUD de usuários |
| **refactor** | Refatoração de código | **carrinho** | Carrinho de compras |
| **test**     | Adicionar ou corrigir testes | **pedido** | Checkout e pedidos |
| **chore**    | Configuração, dependências, build | **messaging** | Fila, RabbitMQ, consumidores |
| **chore**    | Configuração, dependências, build | **docker** | Dockerfile, docker-compose |
| **chore**    | Configuração, dependências, build | **infra** | Configurações gerais |

#### Exemplos Práticos · Examples

* **Funcionalidades e Correções:**
    * `feat(auth): implement login with JWT`
    * `feat(messaging): configure RabbitMQ and publish order event`
    * `fix(carrinho): avoid duplicate items in cart`
    * `fix(auth): fix expired token validation`

* **Refatoração, Testes e Outros:**
    * `refactor(produto): extract validation logic to service`
    * `test(pedido): add integration tests with testcontainers`
    * `docs: add architecture diagram to README`
    * `chore: configure docker-compose with PostgreSQL and RabbitMQ`

#### Regras de Ouro
1. **Inglês sempre!**
2. **Minúsculo** – Tudo em letras minúsculas.
3. **Imperativo** – "add" e não "added" ou "adding".
4. **Curto** – Até 50 caracteres na mensagem principal.
5. **Sem ponto final** – Não termine a linha de resumo com ponto `.`.

## Contributors · Colaboradores

| Name | Role | Contributions |
|------|------|---------------|
| **Diego Ferreira** | Coordinator and Developer | RabbitMQ, checkout, Docker, architecture, Java, Spring boot, React, Redis |
| **Pablo Santos** | Developer | React, Front-end, TypeScript & JavaScript |
| **Max Zimmerman** | Developer | Java, Spring Boot, CRUDs, JWT auth, cart, Swagger, Docs |
| **Giovanna Caxias** | Junior Developer | CRUD, JWT auth, cart, Swagger |

---

## License · Licença

Portfolio project. Not licensed for commercial use.
Projeto de portfólio. Não licenciado para uso comercial.
