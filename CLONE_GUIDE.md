# CLONE_GUIDE (v0.2.0)

## 1. 목적
- v0.2.0 기준 계정/인증/기본 프로필 기능을 빠르게 클론하고 실행하기 위한 안내서다.
- 백엔드/프런트엔드/인프라와 JWT 시크릿 환경변수를 한 번에 검증한다.

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
  - REST 예시: `/api/auth/register`로 회원가입 후 `/api/users/me` 조회

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

## 8. 버전별 메모 (v0.2.0)
- 주요 기능: JWT 기반 회원가입/로그인/로그아웃, 내 프로필 조회/수정, 보호된 라우트.
- WebSocket 에코는 JWT 토큰을 동반해 연결해야 하며, 응답에 닉네임이 포함된다.
