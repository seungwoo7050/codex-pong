# CODING_GUIDE.md

This document defines coding conventions for **all code** in this repository:

- Backend (Spring Boot)
- Frontend (React + TypeScript)
- Realtime (WebSocket / game logic)
- Infra (Docker, Nginx, DB/Redis configs, monitoring)

It is written in English for AI/tooling.  
**All comments and human-facing documentation MUST be written in Korean**, as described below.

---

## 1. Global rules

### 1.1 Language policy (MANDATORY)

- **All comments in source code MUST be written in Korean.**
  - Class/module headers
  - Function/method comments
  - Inline comments
- **All human-facing documentation MUST be written in Korean.**
  - `design/` documents
  - `CLONE_GUIDE.md`
  - Project-level READMEs / guides

- Code identifiers MUST be in English:
  - Package names, class names, function names, variables, file names.
- Protocol tokens and standard terms MUST remain in their typical form:
  - Example: `GET`, `POST`, `WebSocket`, `ON_MESSAGE`.
- Explanations around these tokens MUST be in Korean.

For exact comment formats and templates, see **`DOC_TEMPLATES.md`**.  
If you accidentally add English comments/docs, you MUST rewrite them in Korean.

---

### 1.2 Version tags in code and docs

- For any significant module/file, include a **version tag** in the header comment:
  - Example: `버전: v0.1.0`
- Reference the relevant design document path(s) in the header:
  - Example: `관련 설계문서: design/backend/v0.1.0-auth-and-core-game.md`
- Keep version tags and doc references in sync with `VERSIONING.md`.

---

### 1.3 Directory layout (expected)

Top-level layout will gradually converge to:

```text
backend/        # Spring Boot project
frontend/       # React + TypeScript SPA
infra/          # Docker, Nginx, DB/Redis, monitoring configs

design/         # Korean design docs
  backend/
  frontend/
  realtime/
  infra/
```

Agents should create/modify files inside these directories, not at the root, unless explicitly required.

---

## 2. Backend (Spring Boot)

### 2.1 Tech baseline

* Language: **Java 17+** or **Kotlin** (choose one and keep it consistent).
* Framework: **Spring Boot 3.x**.
* Persistence: Spring Data JPA (Hibernate) with **MariaDB**.
* Cache/ephemeral: Redis.

### 2.2 Project structure

Use a layered architecture. Example package structure (Java):

```text
com.example.transcendence
  ├─ config
  ├─ common
  ├─ user
  ├─ auth
  ├─ game
  ├─ match
  ├─ rank
  ├─ social
  ├─ chat
  ├─ tournament
  ├─ admin
  └─ realtime
```

Inside each domain package, separate responsibilities:

```text
user/
  controller/
  service/
  domain/
  repository/
  dto/
```

* `controller`:

  * REST/WebSocket endpoints.
* `service`:

  * Application/service layer. Orchestrates use cases.
* `domain`:

  * Entities, value objects, domain services.
* `repository`:

  * JPA repositories / custom queries.
* `dto`:

  * API request/response models.

### 2.3 Naming conventions (backend)

* Packages: lowercase, dot-separated.

  * `com.example.transcendence.user.service`
* Classes: `PascalCase`.

  * `UserService`, `GameRoom`, `MatchHistoryService`
* Methods: `camelCase`.

  * `createUser`, `joinMatch`, `updateRating`
* Variables/fields: `camelCase`.

  * `currentUser`, `matchId`, `gameState`
* Constants: `SCREAMING_SNAKE_CASE`.

  * `DEFAULT_PAGE_SIZE`, `MAX_ACTIVE_ROOMS`

No Korean identifiers. Only comments/docs are Korean.

### 2.4 Controller design

* REST controllers:

  * Annotate with `@RestController`.
  * Group endpoints by domain:

    * `UserController`, `AuthController`, `MatchController`, etc.
  * Use clear, RESTful routes:

    * `/api/users`, `/api/auth/login`, `/api/matches`, `/api/rankings`, etc.
* Response format:

  * Prefer a consistent envelope (to be defined in design docs), e.g.

    * `{ "data": ..., "error": null }` or similar.
* Validation:

  * Use Bean Validation (e.g. `@Valid`, `@NotNull`, etc.).
  * Validation errors mapped to consistent error responses.

### 2.5 Service and domain

* Service layer:

  * Contains business logic orchestration.
  * Avoid placing heavy logic directly in controllers or repositories.
* Domain layer:

  * Entities and aggregates should encapsulate invariants where possible.
  * Avoid anemic domain where feasible, but keep it pragmatic.

### 2.6 Persistence

* Entities:

  * Annotated with JPA annotations (`@Entity`, `@Table`, etc.).
  * Use UUID or numeric primary keys as appropriate.
* Queries:

  * Prefer Spring Data repository methods where possible.
  * Use explicit query methods or `@Query` only when necessary.

DB schema and mappings should be described in Korean in `design/backend` docs.

### 2.7 Error handling

* Use a centralized exception handler (`@ControllerAdvice`).
* Define common API error response structure:

  * `code`, `message`, `details`, etc.
* When throwing exceptions:

  * Use clear, English class names (e.g. `UserNotFoundException`) but
  * Comments describing when/why to throw MUST be in Korean.

---

## 3. Frontend (React + TypeScript)

### 3.1 Tech baseline

* React + TypeScript.
* Build tool: Vite (preferred).
* State:

  * Server state: React Query (TanStack Query) or equivalent.
  * Local UI state: lightweight store (Zustand/Context/etc.)

### 3.2 Project structure

Example:

```text
frontend/
  src/
    app/              # app-level bootstrap, routing
    features/
      auth/
      profile/
      lobby/
      game/
      match/
      social/
      chat/
      tournament/
      admin/
    shared/
      components/
      hooks/
      api/
      types/
      utils/
```

### 3.3 Naming conventions (frontend)

* Components: `PascalCase` file and component names.

  * `GameCanvas.tsx`, `LobbyPage.tsx`, `ProfileCard.tsx`
* Hooks: `useXxx` pattern.

  * `useAuth`, `useMatchmaking`, `useWebSocket`
* Types: `PascalCase` for interfaces/types.

  * `UserProfile`, `MatchSummary`, `GameEventPayload`
* Files:

  * One main component per file where reasonable.
  * Group related hooks/components under the same feature directory.

### 3.4 Styling / UI

* Styling approach (CSS Modules / Tailwind / styled-components, etc.) should be chosen once and documented in `design/frontend/initial-design.md`.
* Comments in UI code should explain **why** a layout/component behaves in a specific way (especially for Korean typography/UX) in Korean.

### 3.5 API integration

* Use dedicated API layer in `shared/api`:

  * Example: `authApi.ts`, `userApi.ts`, `matchApi.ts`
* Do NOT call `fetch` or low-level HTTP clients directly from deep inside components.
* Use React Query (or equivalent) for data fetching:

  * Keep cache keys consistent.
  * Handle loading/error states explicitly.

---

## 4. Realtime (WebSocket / game engine)

### 4.1 Transport

* Follow `STACK_DESIGN.md`:

  * Spring WebSocket/STOMP or raw WebSocket, chosen once and documented.
* Frontend:

  * Wrap WebSocket/STOMP client in a reusable hook/class.
  * Do not scatter raw WebSocket calls across many components.

### 4.2 Protocol design

* Event names, payload schemas, and flows MUST be defined in `design/realtime` docs (Korean).
* Code should:

  * Use enum-like constants or string literals for event names.
  * Centralize event handling logic where possible.

Example naming (in code):

* Events from client:

  * `PLAYER_INPUT`, `JOIN_MATCH`, `LEAVE_MATCH`
* Events from server:

  * `GAME_STATE_UPDATE`, `MATCH_START`, `MATCH_END`

Comments describing the semantics MUST be in Korean.

### 4.3 Game loop / engine

* Implement a clear game loop abstraction:

  * Tick rate, update order (physics → collision → scoring, etc.).
* Isolate core game logic from networking:

  * Allows easier testing.
* For tests:

  * Provide deterministic tests for game state transitions.

---

## 5. Infra (Docker, Nginx, DB/Redis, monitoring)

### 5.1 Docker / Compose

* All services (frontend, backend, db, redis, nginx, monitoring) should have:

  * A clear service name.
  * Environment variables documented in Korean in `CLONE_GUIDE.md`.
* Dockerfiles:

  * Multi-stage builds for backend/frontend when appropriate.
  * Comments in Korean for non-obvious build steps.

### 5.2 Nginx

* Config files:

  * Keep directives in standard Nginx syntax.
  * Add Korean comments explaining:

    * Routing rules
    * Timeouts
    * gzip settings
    * Logging formats

### 5.3 DB/Redis configs

* MariaDB:

  * Config tuned for `utf8mb4` and KST.
  * Non-trivial parameters commented in Korean.
* Redis:

  * Persistence, eviction, and security settings (if used) documented in Korean comments.

### 5.4 Monitoring

* When monitoring stack is introduced (per `VERSIONING.md`):

  * Prometheus targets/configs with Korean comments.
  * Grafana dashboards described in Korean in design docs.

---

## 6. Testing

### 6.1 Backend tests

* Use a standard Java/Kotlin test framework (JUnit, etc.).
* Organize tests parallel to source packages:

  * `com.example.transcendence.user` ↔ `com.example.transcendence.user` tests pkg.
* Include:

  * Unit tests for key services/domain logic.
  * Integration tests for important workflows (e.g. matchmaking, ranking updates).

Test method names can be in English; comments inside tests MUST be in Korean if any.

### 6.2 Frontend tests

* Use a standard React testing approach (Jest, Testing Library, etc.).
* Focus on:

  * Critical flows (login, game lobby, starting a match).
  * Component behavior that’s hard to validate manually.

### 6.3 Realtime tests

* Provide at least:

  * Unit tests for core game logic (no network).
  * If feasible, integration tests for protocol handlers.

---

## 7. Agent behavior in coding tasks

When an AI agent writes or modifies code:

1. It MUST:

   * Follow this `CODING_GUIDE.md`.
   * Follow `STACK_DESIGN.md` for architecture.
   * Follow `PRODUCT_SPEC.md` for features.
   * Follow `AGENTS.md` for workflow and documentation rules.

2. For each change set:

   * Focus on **one version** from `VERSIONING.md`.
   * Keep scope aligned with that version’s description.

3. After changing code:

   * Ensure:

     * Korean comments exist and are consistent.
     * Any new public API is reflected in design docs (after tests pass).
     * Build/test commands (documented in Korean in `CLONE_GUIDE.md`) still work.

4. Agents MUST NOT:

   * Introduce a new stack/major library without updating `STACK_DESIGN.md` and getting human approval.
   * Change these conventions silently.

---

## 8. Cross-references

* For **what to build**:

  * `PRODUCT_SPEC.md`
* For **how to architect and which technologies to use**:

  * `STACK_DESIGN.md`
* For **comment/document templates and examples**:

  * `DOC_TEMPLATES.md`
* For **version ordering and scope**:

  * `VERSIONING.md`
* For **agent workflow and constraints**:

  * `AGENTS.md`

All code in this repo MUST respect this `CODING_GUIDE.md` and remain consistent with those documents.