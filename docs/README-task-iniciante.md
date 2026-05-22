# Task de onboarding — colaborador iniciante

Este ficheiro descreve **uma única tarefa pequena e fechada** para quem está a começar no repositório. O objetivo é maximizar aprendizagem com **mínimo risco** e **revisão rápida** (sem alterar backend, sem novas dependências).

---

## Contexto do projeto (o que é isto)

**OrderFlow Commerce** é um monorepo de e-commerce orientado a eventos:

| Parte | Pasta | Estado atual (resumo) |
|--------|--------|-------------------------|
| **API** | `api/` | Spring Boot: CRUD de categorias e produtos, entidades de pedido, RabbitMQ (publicação e consumidores simulados), Swagger, tratamento global de erros. |
| **Web** | `web/` | React 19 + TypeScript + Tailwind: página mínima que só testa `GET /test/ping` através de `apiGet` em `web/src/lib/api.ts`. |

O README principal na raiz (`README.md`) tem diagramas C4, endpoints e roadmap — vale a pena ler pelo menos a secção **REST API Endpoints** e **Quick Start**.

---

## O que já está implementado (relevante para esta task)

- Endpoint **`GET /categories`** — devolve uma lista JSON de categorias com campos `id` e `name` (ver `CategoryController` e entidade `Category` no backend).
- Cliente HTTP no frontend: `getApiBaseUrl()` e `apiGet<T>()` em `web/src/lib/api.ts`.
- Padrão de UI e estado em `web/src/App.tsx` (botão, `loading`, mensagem de resultado / erro).

Não é necessário mexer na API nem no Docker para concluir a task, desde que o ambiente local já consiga correr a Web e a API (ver `AGENTS.md`).

---

## Objetivo da task (entregável)

**Mostrar a lista de categorias na aplicação web**, de forma semelhante ao botão que já chama `/test/ping`:

1. Adicionar um controlo na interface (por exemplo um botão **“Carregar categorias”**) que, ao clicar, chama **`GET /categories`** usando **`apiGet`** (o caminho deve ser `'/categories'` — o prefixo `/api` já vem da base URL).
2. Apresentar o resultado na página: por exemplo uma lista simples com **`id`** e **`name`** de cada categoria (lista HTML ou texto formatado, desde que legível).
3. Tratar erro de rede / HTTP da mesma forma que o exemplo do ping (mensagem ao utilizador, sem deixar a app “presa” em loading).

---

## Escopo fechado — o que fazer e o que não fazer

### Fazer (apenas isto)

- Alterar **só** ficheiros dentro de `web/src/` (tipicamente `App.tsx` e, se quiser organizar tipos, um ficheiro pequeno ao lado — opcional).
- Reutilizar **`apiGet`**; não criar outro cliente HTTP.
- Manter o estilo **Tailwind** alinhado com o que já existe na página (cores, espaçamentos, tipografia).

### Não fazer (evita PRs difíceis de rever)

- Não alterar `api/`, `docker-compose.yml`, `pom.xml`, `package.json` (sem novas dependências).
- Não implementar criar/editar/apagar categorias nesta task (só leitura / GET).
- Não adicionar autenticação, rotas, estado global (Redux, etc.) ou bibliotecas UI.
- Não mudar a configuração do proxy Vite nem variáveis de ambiente, salvo indicação explícita da equipa.

---

## Passos sugeridos (ordem)

1. Clonar o repositório e seguir `AGENTS.md` para subir Postgres + RabbitMQ e a API com perfil `local`; na pasta `web/` correr `npm install` e `npm run dev`.
2. Confirmar no browser ou no Swagger que `GET http://localhost:8080/categories` devolve JSON (a Web usa o proxy `/api`, por isso o pedido partirá de `http://localhost:5173/api/categories`).
3. Abrir `web/src/App.tsx` e `web/src/lib/api.ts` e perceber o fluxo do botão do ping.
4. Implementar o botão / lista conforme o objetivo acima.
5. Correr **`cd web && npm run lint`** e **`cd web && npm run build`** e corrigir o que falhar **antes** de abrir o PR.

---

## Critérios de aceite (checklist para o revisor)

- [ ] Com API a correr e categorias na base de dados (ou após criar uma categoria via Swagger `POST /categories`), a página mostra a lista devolvida por `GET /categories`.
- [ ] Existe feedback de **loading** e de **erro** (comportamento previsível).
- [ ] `npm run lint` e `npm run build` na pasta `web/` passam sem erros.
- [ ] Diff limitado ao frontend (`web/src/`), sem ficheiros acidentais nem reformatações massivas de código não relacionado.

---

## Git e mensagens (regra do repositório)

- Branch: seguir o padrão do projeto descrito no `README.md` (ex.: `feat/web-list-categories`).
- Commits em **inglês**, **minúsculas**, **imperativo**, estilo Conventional Commits, por exemplo: `feat(web): list categories on home page`.

---

## Referência rápida de ficheiros

- Página atual: `web/src/App.tsx`
- Cliente API: `web/src/lib/api.ts`
- Endpoint no backend (só leitura para perceber o JSON): `api/src/main/java/com/orderflow/ecommerce/controllers/CategoryController.java`

Quando terminar, abre um PR pequeno e menciona este ficheiro no corpo do PR para o revisor saber que cumpriste o escopo definido aqui.
