# VERSIONING.md

This document defines the **versioning scheme and roadmap** for this project.

It is written in English for AI/tooling.  
**All implementation comments and human-facing documentation (design docs, CLONE_GUIDE, etc.) MUST be written in Korean.**

Authoritative specs:

- **What to build** → `PRODUCT_SPEC.md`
- **How to build it (stack/architecture)** → `STACK_DESIGN.md`
- **How to code** → `CODING_GUIDE.md`
- **Agent workflow & rules** → `AGENTS.md`
- **Comment/doc templates** → `DOC_TEMPLATES.md`

---

## 1. Global versioning rules

- Version format: `MAJOR.MINOR.PATCH` (e.g. `0.1.0`, `1.0.0`).
- `0.x.y`
  - Development / incremental feature additions.
  - APIs and internal structures may evolve.
- `1.0.0`
  - First **portfolio-ready** release:
    - Feature-complete according to the planned scope.
    - Stable enough to demonstrate and discuss.
- `1.x.y`
  - Backward-compatible improvements on top of `1.0.0`.

**PATCH (`X.Y.Z` with only `Z` changed):**

- For bug fixes, refactors, minor documentation/test adjustments.
- MUST NOT introduce completely new major features.
- If a new PATCH version is created, it MUST be recorded here.

**Agents MUST NOT:**

- Invent new MAJOR/MINOR versions that are not defined here.
- Change the purpose/scope of defined versions without explicit human instruction.

Each change set MUST be associated with **exactly one** target version defined below.

---

## 2. Roadmap overview

High-level roadmap (subject to refinement by a human):

- **v0.1.0** – Core skeleton & minimal vertical slice  
- **v0.2.0** – Accounts, basic auth & profile  
- **v0.3.0** – Real-time 1v1 game & simple matchmaking  
- **v0.4.0** – Ranked mode & basic ranking system  
- **v0.5.0** – Friends, invitations, basic social graph  
- **v0.6.0** – Chat (DM, lobby, match chat)  
- **v0.7.0** – Tournaments & events (simple bracket)  
- **v0.8.0** – Spectator mode & basic live views  
- **v0.9.0** – Admin/ops tooling & monitoring stack  
- **v0.10.0** – Korean-market refinements (OAuth, KST, UX polish)  
- **v1.0.0** – Portfolio-ready release (polish & hardening)

Details are specified per version below.

---

## 3. v0.1.0 – Core skeleton & minimal vertical slice

**Status**: completed (skeleton, health check, demo game flow, WebSocket echo, Docker Compose)

**Goal**

Establish the full-stack skeleton (backend + frontend + infra) and deliver a **minimal playable flow**:

- Simple 1v1 game with manual room join (no ratings, no friends).
- No full account system yet; use a temporary/nickname-based identity.

**Scope**

- Backend:
  - Spring Boot project structure.
  - Health check endpoint (e.g. `GET /api/health`).
  - Minimal game-related API for:
    - Creating a test match/room.
    - Reporting a test game result.
  - In-memory or very simple DB schema for game results.

- Frontend:
  - React/TypeScript project with routing set up.
  - Simple pages:
    - Landing page.
    - Test lobby screen.
    - Minimal game screen (dummy or very simple rendering).

- Realtime:
  - Basic WebSocket wiring:
    - Client can connect to a WebSocket endpoint.
    - Simple echo or placeholder game messages.

- Infra:
  - Docker Compose with:
    - backend
    - frontend
    - db (MariaDB, even if lightly used)
    - redis (optional at this stage)
    - nginx (basic reverse proxy)
  - `CLONE_GUIDE.md` initial version in Korean:
    - Clone, environment variables, `docker compose up`, minimal local run.

**Completion criteria**

- All services build and start via Docker Compose.
- A user can:
  - Open the frontend.
  - Navigate to a test “game” or demo screen.
  - See a minimal interaction flow (even with mocked or simple logic).
- Design docs (Korean):
  - `design/backend/v0.1.0-core-skeleton-and-health.md`
  - `design/frontend/v0.1.0-core-layout-and-routing.md`
  - `design/realtime/v0.1.0-basic-websocket-wiring.md`
  - `design/infra/v0.1.0-local-dev-stack.md`
- `CLONE_GUIDE.md` exists and follows `DOC_TEMPLATES.md`.
- Core modules have Korean comments with version tags.

---

## 4. v0.2.0 – Accounts, authentication & basic profile

**Status**: completed (JWT 인증, 기본 프로필, 보호 라우트/웹소켓 연동, 문서/가이드 업데이트)

**Goal**

Introduce real user accounts and basic profiles, moving away from “temporary nickname only”.

**Scope**

- Backend:
  - User entity & repository.
  - Auth flows:
    - Register / login / logout.
    - Session or JWT-based auth (as decided in design).
  - Basic profile:
    - Nickname
    - Avatar (string/URL or preset)
    - Created/updated timestamps.

- Frontend:
  - Screens:
    - Register / login forms.
    - “My profile” page.
  - Auth state management:
    - Persisted login state (e.g. localStorage + React Query).
    - Guarded routes for authenticated features.

- Realtime:
  - Associate WebSocket connections with authenticated users.

- Infra:
  - DB migrations/schema updates for users.
  - Update `CLONE_GUIDE.md` with any new env variables (DB credentials, auth secrets).

**Completion criteria**

- A user can:
  - Register, log in, log out.
  - View/edit a basic profile.
- Auth is enforced for any “user-owned” game actions.
- Design docs (Korean):
  - `design/backend/v0.2.0-auth-and-profile.md`
  - `design/frontend/v0.2.0-auth-and-profile-ui.md`
- `CLONE_GUIDE.md` updated for new requirements.
- All relevant code commented in Korean.

---

## 5. v0.3.0 – Real-time 1v1 game & simple matchmaking

**Status**: completed (빠른 대전/실시간 1:1 경기)

**Goal**

Deliver an actual playable **real-time 1v1 Pong-like game** with simple matchmaking.

**Scope**

- Backend:
  - Game room management:
    - Create/destroy game rooms.
    - Track players in each room.
  - Game engine:
    - Authoritative game loop (ball/paddle physics, scoring).
  - Simple matchmaking:
    - “Quick play” queue that pairs two players.
  - Game result persistence:
    - Store match results linked to users.

- Frontend:
  - Lobby:
    - “Quick play” button.
    - Simple indication of matchmaking in progress.
  - Game UI:
    - Render paddles/ball using data from backend.
    - Display scores and game end result.

- Realtime:
  - WebSocket events:
    - Client input → server (paddle movement, ready state).
    - Server → client (state updates, start/end signals).
  - Event contracts documented in `design/realtime`.

- Infra:
  - Tune timeouts for WebSocket connections in Nginx/backend config.

**Completion criteria**

- Two logged-in users can:
  - Queue for a game from the lobby.
  - Be matched into a 1v1 room.
  - Play a full game (start → play → end).
  - Have the result recorded in DB.

- Design docs (Korean):
  - `design/backend/v0.3.0-game-and-matchmaking.md`
  - `design/frontend/v0.3.0-game-lobby-and-play-ui.md`
  - `design/realtime/v0.3.0-game-loop-and-events.md`

- Automated tests:
  - Core game engine logic.
  - Simple matchmaking flow.

---

## 6. v0.4.0 – Ranked mode & basic ranking system

**Status**: completed (랭크 큐, 레이팅 갱신, 기본 리더보드)

**Goal**

Introduce a ranked queue and a simple rating system.

**Scope**

- Backend:
  - Rating field for users (ELO/MMR-style).
  - Ranked queue separate from normal queue.
  - Rating update algorithm after each ranked game.
  - Basic leaderboard queries.

- Frontend:
  - UI for:
    - Ranked queue entry.
    - Displaying current rating and rank on profile.
    - Simple leaderboard view.

- Realtime:
  - Ensure ranked games are clearly distinguished from normal games in events and persistence.

**Completion criteria**

- A user can:
  - Play normal games and ranked games separately.
  - See rating changes after ranked matches.
  - View a basic global leaderboard.

- Design docs (Korean):
  - `design/backend/v0.4.0-ranking-system.md`
  - `design/frontend/v0.4.0-ranking-and-leaderboard-ui.md`

---

## 7. v0.5.0 – Friends & invitations (basic social graph)

**Status**: completed (friends, blocks, invitations)

**Goal**

Add core social features: friends, blocks, invitations.

**Scope**

- Backend:
  - Friends relationships:
    - Friend requests, accept/reject.
  - Block list:
    - Prevents friend requests and DMs from blocked users.
  - Game invitations:
    - Invite friend to a normal match.

- Frontend:
  - Friend list:
    - Online/offline status.
    - Buttons for invite and DM.
  - Friend request UI:
    - Pending list, accept/reject.
  - Basic block/unblock UI.

- Realtime:
  - Optional push notifications for friend-related events via WebSocket.

**Completion criteria**

- Users can:
  - Send/accept/reject friend requests.
  - Invite a friend into a match.
  - Block/unblock other users.

- Design docs (Korean):
  - `design/backend/v0.5.0-friends-and-blocks.md`
  - `design/frontend/v0.5.0-friends-and-invites-ui.md`

---

## 8. v0.6.0 – Chat (DM, lobby chat, match chat)

**Status**: completed (DM/로비/매치 채팅, 기본 뮤트 훅, WebSocket 이벤트 정의)

**Goal**

Enable text communication between users.

**Scope**

- Backend:
  - Direct messages (DM) between users.
  - Lobby/global chat channel.
  - Match room chat associated with ongoing games.
  - Basic moderation hooks (e.g. mute per user/channel).

- Frontend:
  - DM UI integrated with friends list.
  - Lobby chat panel.
  - In-game chat overlay.

- Realtime:
  - WebSocket events for chat:
    - Send message
    - Receive message
  - Message delivery guarantees as defined in design docs.

**Completion criteria**

- Users can:
  - Send DMs to friends.
  - Participate in a lobby/global chat.
  - Chat within a match room.
- Admin can:
  - At least mute specific users (internal API or admin UI).

- Design docs (Korean):
  - `design/backend/v0.6.0-chat-and-channels.md`
  - `design/frontend/v0.6.0-chat-ui.md`
  - `design/realtime/v0.6.0-chat-events.md`

---

## 9. v0.7.0 – Tournaments & events (simple bracket)

**Status**: completed (단일 제거 토너먼트 생성/참여/진행, 실시간 알림/브래킷 UI 포함)

**Goal**

Support basic tournament play.

**Scope**

- Backend:
  - Tournament entity:
    - Rules: simple single-elimination bracket.
  - Tournament lifecycle:
    - Create, join, start, progress rounds.
  - Integration with existing game/match system.

- Frontend:
  - Tournament creation/join UI.
  - Simple bracket display.
  - State indicators (upcoming matches, completed rounds).

- Realtime:
  - Tournament-related notifications (match ready, next round, etc.) via WebSocket.

**Completion criteria**

- Users can:
  - Create a small tournament.
  - Join and play through bracket matches.
- Tournament progress is visible and stored.

- Design docs (Korean):
  - `design/backend/v0.7.0-tournaments.md`
  - `design/frontend/v0.7.0-tournament-ui.md`
  - `design/realtime/v0.7.0-tournament-events.md`

---

## 10. v0.8.0 – Spectator mode & live viewing

**Goal**

Allow users to watch ongoing matches.

**Scope**

- Backend:
  - Spectator registration for a match.
  - Broadcast game state to spectators (possibly with small delay).
  - Limit number of spectators per match, with configurable thresholds.

- Frontend:
  - Spectator view:
    - Watch games without controlling paddles.
  - Entry points:
    - From friend list, leaderboard, or dedicated “ongoing matches” list.

- Realtime:
  - Different event channels or roles for spectators vs players.

**Completion criteria**

- Users can:
  - Join a game as a spectator.
  - Watch the game’s progress in real-time (or near real-time).
- Limits and performance constraints documented in design docs.

- Design docs (Korean):
  - `design/backend/v0.8.0-spectator-mode.md`
  - `design/frontend/v0.8.0-spectator-ui.md`
  - `design/realtime/v0.8.0-spectator-events.md`

---

## 11. v0.9.0 – Admin/ops tooling & monitoring stack

**Goal**

Add minimal yet useful operations features and monitoring.

**Scope**

- Backend:
  - Admin APIs:
    - View user and match history.
    - Ban/suspend/mute users.
    - View basic system stats.
  - Structured logging (JSON) for important events.

- Frontend:
  - Admin console UI (can be minimal but functional).

- Infra:
  - Monitoring stack:
    - Prometheus scraping backend metrics.
    - Grafana with simple dashboards (API latency, error rates, active games, etc.).
  - Nginx/infra logs adjusted for basic analysis.

**Completion criteria**

- Admins can:
  - View problem users and take action.
  - Inspect recent matches.
- Monitoring:
  - At least one dashboard showing key metrics.
- Design docs (Korean):
  - `design/backend/v0.9.0-admin-and-ops.md`
  - `design/infra/v0.9.0-monitoring-stack.md`

---

## 12. v0.10.0 – Korean-market refinements

**Goal**

Align UX, auth, and infra more closely with Korean usage patterns.

**Scope**

- Auth:
  - Optionally add Kakao/Naver OAuth login.
  - Keep legacy login working in parallel.

- UX:
  - Korean language texts finalized.
  - Korean-friendly typography and layout refinements.
  - KST-oriented time display.

- Infra:
  - Confirm all services run correctly with:
    - `TZ=Asia/Seoul`
    - `utf8mb4` end-to-end (DB, API, frontend).

**Completion criteria**

- Kakao/Naver login (if included) works end-to-end.
- All major screens are Korean-localized and readable.
- Design docs (Korean):
  - `design/backend/v0.10.0-kor-auth-and-locale.md`
  - `design/frontend/v0.10.0-kor-ux.md`
  - `design/infra/v0.10.0-kst-and-utf8mb4.md`

---

## 13. v0.11.0 – Match replay recording & replay browser (MVP)

**Status**: planned

**Goal**

Users can revisit finished matches via a **replay** page:
- Record match events deterministically (ball/paddle states or input stream).
- Store and browse replays per user.

**Scope**

- Backend:
  - DB schema:
    - `replay` table:
      - `replay_id` (PK)
      - `match_id` (FK)
      - `owner_user_id` (FK)
      - `created_at`
      - `duration_ms`
      - `event_format` (e.g., `JSONL_V1`)
      - `storage_uri` (where replay event file is stored)
      - `checksum` (integrity)
  - Recording:
    - On match end, write replay event stream to storage (initially local volume; object storage can be introduced later).
    - Ensure replay data is not affected by locale/encoding (Korean nicknames included in metadata only).
  - APIs:
    - `GET /api/matches/{matchId}/replay` (returns replay metadata + access permission)
    - `GET /api/replays` (list my replays; paging/sorting)
    - `GET /api/replays/{replayId}` (replay metadata + signed URL or streaming endpoint for event file)

- Frontend:
  - “My Replays” screen:
    - list with paging (match date, opponent, result, duration)
    - search by opponent nickname
  - Replay viewer:
    - renders replay using the same game canvas component (read-only mode)
    - basic controls: play/pause, seek, speed (0.5x/1x/2x)

- Realtime:
  - No new realtime requirement (replay is post-match), but reuse existing WebSocket stack without breaking it.

- Infra:
  - Add a persistent storage path for replay event files in Docker Compose (named volume).
  - Add retention rule (e.g., keep last N replays per user; document policy).

**Completion criteria**

- A user can:
  - Finish a match and see it appear in “My Replays”.
  - Open a replay and watch it from start to end.
  - Seek and change playback speed.
- Design docs (Korean):
  - `design/backend/v0.11.0-replay-recording-and-storage.md`
  - `design/frontend/v0.11.0-replay-browser-and-viewer.md`
  - `design/infra/v0.11.0-replay-storage-and-retention.md`

---

## 14. v0.12.0 – Replay export pipeline via IPC worker (jobs + progress)

**Status**: planned

**Goal**

Export a replay into downloadable artifacts without blocking the backend:
- Export MP4 (or GIF) and a thumbnail.
- Show progress to the user in real time.
- Execute heavy work in an isolated worker via **IPC**.

**Scope**

- Backend:
  - DB schema:
    - `job` table:
      - `job_id` (PK)
      - `job_type` (e.g., `REPLAY_EXPORT_MP4`, `REPLAY_THUMBNAIL`)
      - `owner_user_id`
      - `target_replay_id`
      - `status` (`QUEUED/RUNNING/SUCCEEDED/FAILED/CANCELLED`)
      - `progress` (0..100)
      - `created_at/started_at/ended_at`
      - `error_code/error_message`
      - `result_uri` (download location)
  - APIs:
    - `POST /api/replays/{replayId}/exports/mp4` -> returns `jobId`
    - `POST /api/replays/{replayId}/exports/thumbnail` -> returns `jobId`
    - `GET /api/jobs/{jobId}` -> status/progress
    - `GET /api/jobs` -> list my jobs (paging)
    - `GET /api/jobs/{jobId}/result` -> download when ready
  - Dispatcher:
    - Publish job messages to queue with `jobId` as correlation id.
    - Handle retries and idempotency (same `jobId` must not create duplicated artifacts).

- Worker (new service):
  - Runs as a separate container `replay-worker`.
  - Consumes queue messages, executes export, uploads artifact, publishes progress/result.

- IPC (explicit):
  - Queue mechanism: Redis Streams (or equivalent, but must be one explicit mechanism).
  - Message schema:
    - request: `{ jobId, jobType, replayId, options }`
    - progress: `{ jobId, progress, phase, message }`
    - result: `{ jobId, status, resultUri, checksum, errorCode, errorMessage }`

- Realtime:
  - WebSocket progress:
    - backend pushes `job.progress`, `job.completed`, `job.failed` to the user.
  - Client subscribes by `jobId`.

- Frontend:
  - Replay viewer adds:
    - “Export MP4” button -> creates job -> opens job drawer
    - Job drawer shows progress bar + log lines + download link
  - “Jobs” list page:
    - filter by status/type, retry (if allowed), cancel (if allowed)

- Infra:
  - Docker Compose adds `replay-worker` service.
  - Basic monitoring of:
    - queue depth
    - job duration
    - worker restarts

**Completion criteria**

- A user can:
  - Click “Export MP4” and see a progress bar live via WebSocket.
  - Download the exported file when completed.
  - See a clear error reason if failed.
- Worker crash does not crash backend; job ends in a consistent state (retry or fail with reason).
- Design docs (Korean):
  - `design/backend/v0.12.0-jobs-api-and-state-machine.md`
  - `design/realtime/v0.12.0-job-progress-events.md`
  - `design/infra/v0.12.0-worker-and-queue-topology.md`

---

## 15. v0.13.0 – GPU-accelerated replay rendering + optional HW-encode export

**Status**: planned

**Goal**

Add hardware acceleration paths with strict CPU fallback:
- Client replay rendering uses GPU when available.
- Export pipeline optionally uses hardware encode when host supports it.

**Scope**

- Frontend:
  - Replay renderer:
    - Implement WebGL rendering path (GPU) for replay visualization.
    - Provide fallback to Canvas2D when WebGL is unavailable.
  - Performance instrumentation:
    - record average FPS during replay playback (1x, 2x) in dev mode.

- Worker:
  - Export MP4:
    - add config flag `EXPORT_HW_ACCEL=true/false`
    - detect supported hw encoder/decoder on startup and log capability
    - if unsupported, automatically fall back to software encode
  - Output determinism:
    - exported file must be playable and consistent across hw/software (within acceptable encoding variance)

- Infra:
  - Document local optional GPU setup (manual checklist, not required for CI).
  - Keep CI pipeline CPU-only.

**Completion criteria**

- Replay playback uses WebGL path by default when available, and fallback works.
- In a GPU-capable environment, export uses HW acceleration and shows measurable speedup vs software (documented).
- In CPU-only environment, export still completes successfully.
- Design docs (Korean):
  - `design/frontend/v0.13.0-webgl-replay-renderer.md`
  - `design/infra/v0.13.0-optional-gpu-runtime.md`
  - `design/backend/v0.13.0-export-hw-accel-flags.md`

---

## 16. v0.14.0 – Frontend reflow audit + async patterns hardening (portfolio-grade)

**Status**: planned

**Goal**

Make performance and async code quality explicitly demonstrable:
- Control reflow/layout thrashing on heavy screens.
- Remove callback-style async nesting in core flows.
- Add UTF-8 regression coverage (Korean/emoji) for replay/jobs metadata.

**Scope**

- Frontend:
  - Reflow audit targets (must pick at least 2):
    - leaderboard screen
    - chat screen
    - replay list screen
    - jobs list screen
  - Apply fixes:
    - virtualization or paging for long lists
    - prevent layout thrashing (no interleaved DOM read/write in loops)
    - memoization for stable renders (React)
  - Provide a short perf report in docs with before/after screenshots.

- Backend:
  - UTF-8 regression tests:
    - Korean + emoji nicknames in replay/job metadata
    - REST + WebSocket payload encoding round-trip
    - DB round-trip correctness (building on v0.10.0 utf8mb4 requirement)

- Realtime:
  - Standardize job progress client handling:
    - Promise/async wrappers
    - unified error handling
    - reconnection policy documented

**Completion criteria**

- Perf report exists with:
  - identified reflow sources
  - before/after evidence (DevTools captures)
  - concrete mitigations
- Core async flows contain no deeply nested callbacks (code review rule + refactor).
- UTF-8 regression tests pass consistently.
- Design docs (Korean):
  - `design/frontend/v0.14.0-reflow-audit-and-fixes.md`
  - `design/realtime/v0.14.0-async-ws-client-patterns.md`
  - `design/backend/v0.14.0-utf8-regression-suite.md`

---

## 17. v1.0.0 – Portfolio-ready release

**Goal**

Provide a **stable, well-documented** release suitable for portfolio use and technical discussions.

**Scope**

- Stabilize and polish features introduced in v0.1.0–v0.14.0.
- Fix known bugs and performance issues that affect basic usability.
- Ensure all documentation and demos are coherent.

**Completion criteria**

- Feature completeness:
  - Core features from `PRODUCT_SPEC.md` implemented.
  - Advanced features (tournaments, social, chat, basic monitoring) working per design.
- Documentation:
  - High-level overview docs in Korean:
    - `design/backend/v1.0.0-overview.md`
    - `design/frontend/v1.0.0-overview.md`
    - `design/realtime/v1.0.0-overview.md`
    - `design/infra/v1.0.0-overview.md`
  - `CLONE_GUIDE.md` fully up-to-date (clone, run, test).
- Stability:
  - Core flows tested:
    - Register → login → play → view stats.
    - Use social features → chat → join tournaments → spectate matches.
- `VERSIONING.md` updated to clearly mark `v1.0.0` as **portfolio-ready**.

---

## 14. Notes for agents

- For any coding task:
  - Pick **one** version above and follow `AGENTS.md` workflow.
- Do not alter this roadmap unless a human explicitly instructs you to.
- If conflicts arise:
  - `PRODUCT_SPEC.md` defines the intended product behavior.
  - `STACK_DESIGN.md` defines the allowed stack.
  - This `VERSIONING.md` defines **when** each part is implemented.
