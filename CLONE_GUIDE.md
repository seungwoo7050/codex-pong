# CLONE_GUIDE (v0.3.0)

## 1. 목적
- v0.3.0 기준 실시간 1:1 게임/빠른 대전 흐름을 실행하기 위한 안내서다.
- 백엔드/프런트엔드/인프라와 JWT 시크릿, WebSocket 연결을 한 번에 검증한다.

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

## 5. Docker Compose 실행
```bash
docker compose build --progress=plain
docker compose up -d
```
- 접속 경로
  - 웹: http://localhost/
  - 헬스체크: http://localhost/api/health
  - WebSocket: ws://localhost/ws/echo (쿼리 파라미터 `token` 필요)
  - 게임 WebSocket: ws://localhost/ws/game?roomId=<매칭된-방>&token=<JWT>
  - REST 예시: `/api/auth/register`로 회원가입 후 `/api/match/quick`으로 빠른 대전 티켓 발급

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

## 8. 버전별 메모 (v0.3.0)
- 주요 기능: 빠른 대전 큐, 실시간 1:1 게임 루프, 경기 결과 저장/조회.
- 매칭 절차: 로비에서 "빠른 대전" 클릭 → `/api/match/quick` 티켓 발급 → roomId로 `/ws/game` 연결.
- WebSocket 연결에는 JWT 토큰이 필요하며, 120초 타임아웃으로 유지된다.
