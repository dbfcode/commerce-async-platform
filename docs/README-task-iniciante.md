# Task de onboarding — colaborador iniciante (backend Java)

Este ficheiro descreve **uma única tarefa pequena e fechada** em **Spring Boot / Java** para quem está a começar no repositório. O objetivo é aprender o fluxo controller → repositório → resposta JSON, com **revisão rápida** (sem frontend, sem novas dependências Maven).

---

## Contexto do projeto (o que é isto)

**OrderFlow Commerce** é um monorepo de e-commerce orientado a eventos:

| Parte | Pasta | Estado atual (resumo) |
|--------|--------|-------------------------|
| **API** | `api/` | Spring Boot 3.2 / Java 21: CRUD de categorias e produtos, pedidos em JPA, RabbitMQ (eventos e consumidores simulados), Swagger via interfaces `*ControllerDocs`, `GlobalExceptionHandler`. |
| **Web** | `web/` | React mínima (não faz parte desta task). |

Lê `AGENTS.md` para correr Postgres + RabbitMQ e a API com perfil `local`. O `README.md` na raiz resume endpoints e arquitetura.

---

## O que já está implementado (relevante para esta task)

- `CategoryController` em `GET /categories` e `GET /categories/{id}`, etc., implementa `CategoryControllerDocs` (anotações OpenAPI na interface).
- `CategoryRepository` é uma `JpaRepository` — já expõe o método **`count()`** (contagem de linhas na tabela de categorias).
- DTOs como **records** com `@Schema` para o Swagger: exemplo `PingResponse` em `api/src/main/java/com/orderflow/ecommerce/dtos/PingResponse.java`.

---

## Objetivo da task (entregável)

Expor um endpoint **somente leitura** que devolve **quantas categorias** existem na base de dados:

| Método | Caminho | Resposta JSON (exemplo) |
|--------|---------|-------------------------|
| `GET` | `/categories/count` | `{"count": 3}` |

### Implementação esperada (alto nível)

1. **Novo DTO** (record) em `api/src/main/java/com/orderflow/ecommerce/dtos/`, por exemplo `CategoryCountResponse`, com um campo numérico para a contagem (nome do campo JSON: **`count`**). Incluir anotações `@Schema` nos campos, no estilo de `PingResponse`, para o Swagger documentar bem o contrato.
2. **`CategoryControllerDocs`**: adicionar um método abstrato com `@Operation` + `@ApiResponse` 200 descrevendo o novo DTO (igual ao padrão dos métodos já documentados na mesma interface).
3. **`CategoryController`**: implementar o método da interface com `@GetMapping` no caminho **`/count`** (o prefixo `/categories` já vem do `@RequestMapping` da classe). O corpo do método deve usar **`repository.count()`** e devolver `ResponseEntity` com status 200 e o record preenchido.

Não é obrigatório escrever testes automatizados nesta task; se adicionares, mantém-nos **mínimos** (por exemplo um teste `@WebMvcTest` ou um teste de integração simples) e não aumentes o escopo além do endpoint.

---

## Escopo fechado — o que fazer e o que não fazer

### Fazer (apenas isto)

- Alterar **só** código dentro de `api/src/main/java/com/orderflow/ecommerce/` (tipicamente `controllers/`, `controllers/docs/` e `dtos/`).
- Seguir o **mesmo estilo** dos controladores existentes (imports, `ResponseEntity`, anotações Spring).
- Garantir que o endpoint aparece no **Swagger UI** com descrição legível.

### Não fazer (evita PRs difíceis de rever)

- Não alterar `web/`, `docker-compose.yml`, nem ficheiros em `api/src/main/resources/` salvo se for estritamente necessário (nesta task **não** deve ser).
- Não adicionar dependências ao `pom.xml`.
- Não implementar paginação, cache, segurança por roles, nem alterar regras de negócio das categorias existentes.
- Não mudar o contrato dos endpoints CRUD já existentes.

---

## Passos sugeridos (ordem)

1. Subir infra e API com perfil `local` (ver `AGENTS.md`).
2. Estudar `CategoryController`, `CategoryControllerDocs` e `PingResponse`.
3. Criar o record DTO + assinatura em `CategoryControllerDocs` + implementação em `CategoryController`.
4. Validar manualmente no Swagger: `GET /categories/count` devolve JSON com `count` coerente (cria ou apaga categorias com os endpoints já existentes para ver o número mudar).
5. Correr **`cd api && chmod +x mvnw 2>/dev/null; ./mvnw test`** e corrigir falhas **antes** de abrir o PR.

---

## Critérios de aceite (checklist para o revisor)

- [ ] `GET /categories/count` responde **200** e corpo JSON com campo **`count`** (número inteiro não negativo).
- [ ] O valor de **`count`** corresponde ao **`repository.count()`** (ou seja, à tabela de categorias).
- [ ] Swagger mostra o novo endpoint na secção **Category** com resposta documentada.
- [ ] `./mvnw test` na pasta `api/` passa.
- [ ] Diff pequeno e focado (sem reformatação de ficheiros inteiros nem alterações não relacionadas).

---

## Nota técnica (caminho `/categories/count`)

O controlador já tem `GET /categories/{id}`. Em Spring Boot 3.x, o mapeamento **literal** `/categories/count` deve conviver com `/{id}` sem tratar a palavra `count` como ID. Se durante os testes o pedido a `/categories/count` for incorretamente tratado como `/{id}`, para **esta task** basta alinhar com o revisor um caminho alternativo sem ambiguidade (por exemplo prefixo extra); o importante é manter o contrato JSON com **`count`**.

---

## Git e mensagens (regra do repositório)

- Branch: padrão do `README.md` da raiz (ex.: `feat/categoria-category-count-endpoint`).
- Commits em **inglês**, **minúsculas**, **imperativo**, Conventional Commits, por exemplo: `feat(categoria): add category count endpoint`.

---

## Referência rápida de ficheiros

- Controller: `api/src/main/java/com/orderflow/ecommerce/controllers/CategoryController.java`
- Documentação OpenAPI: `api/src/main/java/com/orderflow/ecommerce/controllers/docs/CategoryControllerDocs.java`
- Exemplo de DTO record: `api/src/main/java/com/orderflow/ecommerce/dtos/PingResponse.java`
- Repositório: `api/src/main/java/com/orderflow/ecommerce/repositories/CategoryRepository.java`

Quando terminar, abre um PR pequeno e referencia este ficheiro no corpo do PR para o revisor validar o checklist acima.
