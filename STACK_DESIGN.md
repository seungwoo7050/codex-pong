# STACK_DESIGN.md

This document defines the **authoritative tech stack and architecture** for this project.

> This file is in **English** for AI/tooling.
> **All implementation comments and human-facing documentation (design docs, CLONE_GUIDE, etc.) must be written in Korean**, as defined in `AGENTS.md` / `CODING_GUIDE.md`.

This project is **not** an implementation of the original 42 ft_transcendence subject.  
It is a **Korean-market oriented real-time game service** inspired by that idea, with its own stack and architecture.

---

## 1. High-level architecture

### 1.1 Overview

We design a **monolithic-but-modular** backend with a separate SPA frontend and real-time features:

- **Frontend**
  - React + TypeScript SPA
  - Talks to backend via REST/JSON + WebSocket
- **Backend**
  - Spring Boot (Java 17 or Kotlin)
  - REST API + WebSocket/STOMP endpoint
  - Modular domain layers (User, Auth, Game, Matchmaking, Ranking, Social, Admin, etc.)
- **Data stores**
  - MariaDB (primary relational store)
  - Redis (cache, session, matchmaking/room state)
- **Infra**
  - Nginx as reverse proxy (terminates TLS, static assets, routing)
  - Docker + Docker Compose for local/dev orchestration
- **Observability**
  - Centralized structured logging
  - Basic metrics + dashboards (Prometheus + Grafana or equivalent)
- **Korean-market concerns**
  - Timezone: `Asia/Seoul`
  - Charset: `utf8mb4` (emoji, Korean)
  - Later: Kakao/Naver OAuth integration (versioned in `VERSIONING.md`)

---

## 2. Frontend stack

### 2.1 Framework & language

- **React** (latest stable major) with **TypeScript**
- Build tooling:
  - **Vite** or `create-react-app` alternative; prefer **Vite** for performance.
- UI:
  - Component library may be used (e.g. MUI, Chakra, or Tailwind-based), but must be:
    - Compatible with Korean typography (폰트, 줄바꿈 등)
    - Easy to customize for dark/light themes

### 2.2 Architecture

- SPA structure:
  - Feature/domain folders:
    - `features/auth`
    - `features/profile`
    - `features/game`
    - `features/match`
    - `features/social`
    - `features/admin`
  - Shared:
    - `shared/components`
    - `shared/hooks`
    - `shared/api`
    - `shared/types`

- **State management**
  - Server state:
    - **React Query** (TanStack Query) or equivalent for REST API calls
  - Client-side state:
    - Lightweight store (e.g. Zustand, Context) for UI-only state

- **Realtime**
  - WebSocket client:
    - If backend uses STOMP: use STOMP JS client
    - If backend uses raw WebSocket: custom client with typed events
  - Event contracts must be defined in `design/frontend` + `design/realtime` (in Korean) and strictly followed.

### 2.3 Frontend responsibilities

- UI for:
  - Authentication, profile, settings
  - Game lobby, room creation/join
  - Real-time game view (Pong or variant)
  - Match history, ranking, stats
  - Social (friends, invitations, chat/channels)
  - Admin console (limited)

- Frontend **does not** implement game logic authority; it only renders based on data from backend/game service.

---

## 3. Backend stack

### 3.1 Core technologies

- **Language**
  - Java 17+ or Kotlin (choose one and keep it consistent)
- **Framework**
  - Spring Boot 3.x
- **Persistence**
  - Spring Data JPA (Hibernate) with **MariaDB**
- **Caching & ephemeral data**
  - Redis (via Spring Data Redis or Lettuce)

### 3.2 Application architecture

- Standard layered architecture:

  - `api` (controllers)
  - `application` / `service` (use cases)
  - `domain` (entities/aggregates/value objects)
  - `infrastructure` (repositories, external integrations)
  - `config` (Spring configuration, security, etc.)

- Typical packages:

  - `com.example.transcendence.user`
  - `com.example.transcendence.auth`
  - `com.example.transcendence.game`
  - `com.example.transcendence.match`
  - `com.example.transcendence.rank`
  - `com.example.transcendence.social`
  - `com.example.transcendence.admin`
  - `com.example.transcendence.realtime`
  - `com.example.transcendence.common`

### 3.3 API patterns

- REST API:
  - JSON over HTTP
  - Standard HTTP methods (GET/POST/PATCH/DELETE)
  - Consistent response envelope (e.g. `{ data, error }` pattern) to be defined in design docs.
- Authentication:
  - Sessions or JWT-based auth (exact choice defined in `PRODUCT_SPEC.md` / relevant design docs).
  - Later: OAuth2 login (Kakao/Naver) as separate versions in `VERSIONING.md`.

### 3.4 Error handling

- Use centralized exception handling (Spring `@ControllerAdvice`).
- Define standard error format (code, message, details).
- All error messages intended for logs/documentation must have Korean comments explaining their semantics in code.

---

## 4. Real-time & game services

### 4.1 Real-time transport

- Preferred options:

  - **Spring WebSocket** with STOMP:
    - Simpler integration with Spring ecosystem.
  - Alternatively, pure WebSocket endpoint with custom protocol.

- Choice should be fixed at the beginning and described in `design/realtime/initial-design.md`.

### 4.2 Game architecture

- **Authoritative server model**:
  - Backend determines the true game state.
  - Clients send input events; backend broadcasts state updates.

- Components:

  - Game lobby/matchmaker:
    - Redis-backed queues for matchmaking
    - Room state stored in memory + Redis as needed
  - Game engine:
    - Tick-based game loop (e.g. fixed tick rate)
    - Game rooms handled in-memory (within monolith) for v0.x
  - Real-time channel:
    - WebSocket endpoints for:
      - Match join/leave
      - Game state updates
      - Input events from clients

### 4.3 Scaling strategy (directional, not required at v0.x)

- v0.x:
  - Single monolithic backend instance.
  - All game rooms handled in-process.
- Later versions:
  - Option to separate real-time gateway / game worker processes.
  - This should be added only if/when `VERSIONING.md` defines a corresponding version.

---

## 5. Data stores

### 5.1 Primary DB – MariaDB

- Use **MariaDB** instead of PostgreSQL to align with common Korean hosting environments.
- Settings:
  - Charset: `utf8mb4`
  - Collation: `utf8mb4_unicode_ci` (or an appropriate variant)
  - Timezone: `+09:00` / `Asia/Seoul`
- Usage:
  - Relational data:
    - Users, profiles
    - Match records, rankings
    - Friends/social graph (if relational model is sufficient)
    - Audit logs/ban records

### 5.2 Redis

- Roles:
  - Caching frequently used data (e.g. user presence, profile snippets)
  - Session or token blacklists (depending on auth design)
  - Matchmaking queues
  - Real-time game room metadata (where appropriate)

- Basic guidelines:
  - Use clear key prefixes per domain: `user:`, `match:`, `room:`, etc.
  - Avoid using Redis as the sole source of truth for critical data.

---

## 6. Infra & deployment

### 6.1 Local and dev environment

- Orchestrated via **Docker Compose**:

  - Services:
    - `frontend`
    - `backend`
    - `db` (MariaDB)
    - `redis`
    - `nginx`
    - (later) `monitoring` stack

- Each service has its own Dockerfile:
  - `frontend/Dockerfile`
  - `backend/Dockerfile`
  - `infra/db/Dockerfile` or direct MariaDB image with config
  - `infra/redis/Dockerfile` or direct Redis image with config
  - `infra/nginx/Dockerfile`

- All configuration files (Nginx, DB, etc.) must have Korean comments explaining non-trivial settings.

### 6.2 Nginx

- Roles:
  - Terminate TLS (for production)
  - Route:
    - `/api/*` → backend
    - `/ws/*` → backend WebSocket endpoint
    - `/` → frontend SPA
- Korean-market tuning:
  - Timeouts appropriate for typical Korean latency/bandwidth.
  - Gzip for static assets.
  - Logging format includes request/response times.

### 6.3 Environment configuration

- All containers should use:
  - `TZ=Asia/Seoul`
- Application-level:
  - Default locale/timezone set to Korean standards.
- Secrets:
  - Do not commit real secrets.
  - Use `.env` / environment variables, documented in Korean in `CLONE_GUIDE.md`.

---

## 7. Observability & logging

### 7.1 Logging

- Structured logging from backend:
  - JSON logs where possible (for easy ingestion later).
- Content:
  - Request ID, user ID (when available), endpoint, latency, status code, error codes.
- All log messages in code can remain English tokens, but comments about their meaning must be Korean.

### 7.2 Metrics

- Goal (for later versions as defined in `VERSIONING.md`):

  - Basic metrics:
    - Request counts, error rates
    - Game room counts
    - Active users
  - Tools:
    - Prometheus (scraping metrics)
    - Grafana (dashboards)

- Initial versions (v0.x):
  - Simple in-app metrics or logging-based stats are acceptable, but the stack must be designed so that Prometheus/Grafana can be attached later without major rewrites.

---

## 8. Korean-market considerations

- **Language & encoding**
  - End-to-end `utf8mb4` (DB, backend responses, frontend rendering).
- **Timezone**
  - All timestamps treated primarily in KST (`Asia/Seoul`), even if stored as UTC.
  - API contracts must clearly describe timestamp formats (ISO-8601 etc.).
- **Login**
  - Baseline: ID/Password or email/password.
  - Later versions: Kakao/Naver OAuth integrations (exact endpoints to be defined in `PRODUCT_SPEC.md` and `VERSIONING.md`).
- **UX**
  - Consider typical Korean desktop/mobile usage patterns when designing frontend pages (will be addressed in `design/frontend`).

---

## 9. Technologies not to use

To avoid drifting back to the original 42 stack or mixing paradigms:

- **Do NOT** use:
  - NestJS-based ft_transcendence starter stacks.
  - PostgreSQL as primary DB for this project.
  - Non-Spring backends (e.g. Express/Nest) unless `STACK_DESIGN.md` is explicitly updated by a human.

- Any deviation from this stack (e.g. changing backend language, DB choice) must:
  - Be approved by a human maintainer.
  - Be reflected in **both**:
    - `STACK_DESIGN.md`
    - `VERSIONING.md` (as a new version or migration step)

---

## 10. Relationship with other documents

- `AGENTS.md`
  - Defines how agents must behave and which files they must read.
  - Enforces Korean-only comments and docs.

- `CODING_GUIDE.md`
  - Defines coding conventions for this stack:
    - Naming, layering, error handling, test structure.

- `PRODUCT_SPEC.md`
  - Defines **what** the service does:
    - Features, user flows, domain requirements.

- `VERSIONING.md`
  - Defines **when** features are introduced:
    - Version-by-version roadmap for implementing PRODUCT_SPEC on top of this stack.

- `design/` (per project/module)
  - Captures detailed architecture and data flows for each version, **in Korean**.

All changes to the codebase must be consistent with this `STACK_DESIGN.md`.  
If implementation needs a stack change, this file must be updated first by explicit human decision.
