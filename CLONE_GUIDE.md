# CLONE_GUIDE (v0.9.0)

## 1. 목적
- v0.9.0 기준 실시간 1:1 게임, 랭크 큐/리더보드, 친구/차단/초대, DM/로비/매치 채팅, 단일 제거 토너먼트, **관전 모드/라이브 보기** 흐름을 실행하기 위한 안내서다.
- 관리자 전용 API/콘솔, Prometheus+Grafana 모니터링 스택, JSON 기반 Nginx/백엔드 로그를 포함한다.
- 백엔드/프런트엔드/인프라와 JWT 시크릿, WebSocket 연결, 랭크 레이팅, 소셜/채팅/토너먼트/관전/운영 API를 한 번에 검증한다.
- 본 문서는 `/ws/*` 경로를 사용하는 **raw WebSocket(STOMP 미사용)** 전제를 따른다. 전송 방식 변경 시 `design/realtime/initial-design.md` 및 본 문서를 함께 갱신한다.

## 2. 사전 준비물
- Git
- Docker / Docker Compose
- Node.js 18 (프런트엔드 로컬 실행 시)
- JDK 17 (백엔드 로컬 실행 시)

## 3. 클론 및 기본 구조
```bash
git clone <repo-url>
cd codex-pong
```
- 주요 디렉터리
  - `backend/`: Spring Boot 소스
  - `frontend/`: React + Vite 소스
  - `infra/`: Nginx 설정 등 인프라 자원
  - `design/`: 한국어 설계 문서

## 4. 환경변수
- 백엔드 (docker-compose 기본값)
  - `DB_HOST=db`
  - `DB_NAME=codexpong`
  - `DB_USER=codexpong`
  - `DB_PASSWORD=codexpong`
  - `AUTH_JWT_SECRET` (32바이트 이상, 기본 `change-me-in-prod-secret-please-keep-long`)
  - `AUTH_JWT_EXPIRATION_SECONDS` (선택, 기본 3600)
- 프런트엔드
  - `VITE_BACKEND_URL` (기본 `http://localhost:8080`)
  - `VITE_BACKEND_WS` (기본 `ws://localhost:8080`)
  - 관리자 콘솔은 동일한 백엔드 URL을 사용하며 별도 토큰(운영자)을 요구한다.

## 5. Docker Compose 실행
```bash
docker compose build --progress=plain
docker compose up -d
```
- 접속 경로
  - 웹: http://localhost/
  - 리더보드: http://localhost/leaderboard
  - 관리자 콘솔: http://localhost/admin (운영 토큰 필요)
  - 관전 목록: http://localhost/spectate
  - 헬스체크: http://localhost/api/health
  - WebSocket: ws://localhost/ws/echo (쿼리 파라미터 `token` 필요)
  - 게임 WebSocket: ws://localhost/ws/game?roomId=<매칭된-방>&token=<JWT>
  - 관전 WebSocket: ws://localhost/ws/game?roomId=<진행중-방>&token=<JWT>&role=spectator
  - 소셜 WebSocket: ws://localhost/ws/social?token=<JWT> (친구 요청/초대 이벤트 구독용)
  - 채팅 WebSocket: ws://localhost/ws/chat?token=<JWT> (로비 기본 구독, DM/매치 명령 전송)
  - 토너먼트 WebSocket: ws://localhost/ws/tournament?token=<JWT> (토너먼트 알림 구독)
  - REST 예시: `/api/auth/register`로 회원가입 후 `/api/match/quick`으로 일반전 티켓, `/api/match/ranked`로 랭크전 티켓 발급, `/api/social/friend-requests`로 친구 요청 발송, `/api/match/ongoing`으로 관전 대상 확인
  - 운영/모니터링:
    - 관리자 API: `/api/admin/users`, `/api/admin/stats`, `/api/admin/matches`
    - Prometheus: http://localhost:9090 (스크레이프 타겟 확인)
    - Grafana: http://localhost:3000 (기본 계정 `admin`/`admin`, 대시보드 "Codex Pong 운영 개요")
    - 로그: Nginx/백엔드 모두 JSON 포맷으로 표준 출력, `docker compose logs backend|nginx`로 조회

## 6. 개별 서비스 로컬 실행 (선택)
### 6.1 백엔드
```bash
cd backend
./gradlew bootRun
```
### 6.2 프런트엔드
```bash
cd frontend
npm install
npm run dev -- --host --port 5173
```

## 7. 테스트 실행
### 7.1 백엔드 테스트
```bash
cd backend
./gradlew test
```
### 7.2 프런트엔드 테스트
```bash
cd frontend
npm install
npm test
```
### 7.3 프런트엔드 빌드 확인
```bash
cd frontend
npm install
npm run build
```

## 8. 버전별 메모 (v0.9.0)
- 주요 기능: 일반/랭크 대전, 랭크 레이팅, 리더보드 + 친구 목록/요청/차단/초대, 친구 초대 후 게임 방 진입, DM/로비/매치 채팅, 단일 제거 토너먼트, **관전 모드**에 더해 **관리자 API/콘솔 + Prometheus/Grafana 모니터링**.
- 매칭 절차: 로비에서 원하는 큐 선택 → 일반전 `/api/match/quick`, 랭크전 `/api/match/ranked` 티켓 발급 → roomId로 `/ws/game` 연결.
- 관전 절차: `/api/match/ongoing`으로 진행 중인 roomId 조회 → `/spectate?roomId=<id>` 이동 → WebSocket `role=spectator`로 연결해 입력 없이 상태 수신.
- 토너먼트 절차:
  - 생성: `/api/tournaments` POST (name, maxParticipants=4/8/16)
  - 참여: `/api/tournaments/{id}/join` POST
  - 시작: `/api/tournaments/{id}/start` POST (생성자 전용, 정원 충족 시)
  - 진행: 응답/조회 `/api/tournaments/{id}`로 브래킷 확인, 매치 roomId로 `/ws/game` 접속
- 운영/모니터링:
  - 관리자 API: `/api/admin/users`(목록/상태), `/api/admin/users/{id}/moderations`(밴/정지/뮤트), `/api/admin/stats`, `/api/admin/matches`.
  - Grafana: 기본 데이터 소스 Prometheus, 대시보드 UID `codexpong-admin-overview`.
  - 로그: `logback-spring.xml` 및 Nginx `log_format structured`로 JSON 출력, `docker compose logs`로 수집.
- 소셜/채팅 절차는 v0.8.0과 동일.
