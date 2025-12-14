# CLONE_GUIDE (v0.14.0)

## 1. 목적
- v0.14.0 기준 **리플로우 제어 + WebSocket 비동기 패턴 + UTF-8 회귀 테스트**까지 포함한 전체 스택 실행/검증 안내서다.
- 기존 실시간 게임/소셜/토너먼트/관전/리플레이/잡 파이프라인을 유지하면서, 프런트엔드 성능 캡처 절차와 WebSocket 재연결 정책, UTF-8 테스트를 최신화한다.
- raw WebSocket(`/ws/*`) 전제를 유지한다. 전송 방식 변경 시 `design/realtime/initial-design.md` 및 본 문서를 함께 갱신한다.

## 2. 사전 준비물
- Git
- Docker / Docker Compose
- Docker 데몬 권한: compose 실행 전 `docker version`/`docker compose version`으로 데몬 연결을 확인한다. CI나 제한된 샌드박스에서는
  `dockerd` 기동에 커널 권한(CAP_NET_ADMIN 등)이 없어 실패할 수 있으므로, 그 경우 호스트 제공 도커 환경을 사용하거나 권한이
  허용된 런너에서 실행해야 한다.
- Node.js 18 (프런트엔드 로컬 실행 시)
- JDK 17 (백엔드 로컬 실행 시)
- ffmpeg CLI (로컬에서 워커를 단독 실행할 때 필요, Docker Compose 사용 시 자동 포함)
- GPU 옵션 시험 시: NVIDIA(`nvidia-smi`), VAAPI(`/dev/dri/renderD128`) 등 장치 노출 여부를 사전 확인한다.

## 3. 클론 및 기본 구조
```bash
git clone <repo-url>
cd codex-pong
```
- 주요 디렉터리
  - `backend/`: Spring Boot 소스
  - `frontend/`: React + Vite 소스
  - `worker/`: Redis Streams 기반 replay-worker (ffmpeg 호출)
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
  - `AUTH_KAKAO_PROFILE_URI` (선택, 기본 `https://kapi.kakao.com/v2/user/me`, 테스트용 모킹 시 오버라이드)
  - `AUTH_NAVER_PROFILE_URI` (선택, 기본 `https://openapi.naver.com/v1/nid/me`)
  - `REPLAY_STORAGE_PATH` (기본 `${user.dir}/build/replays`, Docker Compose 시 `/data/replays` named volume)
  - `REPLAY_RETENTION_MAX_PER_USER` (기본 20)
  - `JOB_EXPORT_PATH` (기본 `${REPLAY_STORAGE_PATH}/exports`, 워커와 백엔드가 동일 루트 사용)
  - `REDIS_HOST=redis`, `REDIS_PORT=6379`
  - 잡 큐: `JOB_QUEUE_ENABLED=true`, `JOB_QUEUE_REQUEST_STREAM=job.requests`, `JOB_QUEUE_PROGRESS_STREAM=job.progress`, `JOB_QUEUE_RESULT_STREAM=job.results`, `JOB_QUEUE_CONSUMER_GROUP=replay-jobs`
- 프런트엔드
  - `VITE_BACKEND_URL` (기본 `http://localhost:8080`)
  - `VITE_BACKEND_WS` (기본 `ws://localhost:8080`)
- 워커(로컬 단독 실행 시)
  - `REDIS_HOST`/`REDIS_PORT`
  - `JOB_QUEUE_REQUEST_STREAM`/`JOB_QUEUE_PROGRESS_STREAM`/`JOB_QUEUE_RESULT_STREAM`/`JOB_QUEUE_CONSUMER_GROUP`
  - `WORKER_ID` (로그 구분용)
  - `EXPORT_HW_ACCEL` (선택, 기본 false): true로 설정 시 ffmpeg 하드웨어 인코더를 우선 시도하고 실패하면 CPU(libx264)로 자동 폴백한다.
- 모든 컨테이너 기본 `TZ=Asia/Seoul`, DB 콜레이션 `utf8mb4_unicode_ci` 유지.

## 5. Docker Compose 실행
```bash
docker compose build --progress=plain
docker compose up -d
```
- 서비스
  - `backend`, `frontend`, `db`(MariaDB), `redis`, `replay-worker`, `prometheus`, `grafana`, `nginx`
- 접속 경로
  - 웹: http://localhost/
  - 리플레이 목록: http://localhost/replays (로그인 필요)
  - 작업 목록: http://localhost/jobs (로그인 필요, 진행률 실시간 반영)
  - 리더보드: http://localhost/leaderboard
  - 관리자 콘솔: http://localhost/admin (운영 토큰 필요)
  - 관전 목록: http://localhost/spectate
  - 헬스체크: http://localhost/api/health
  - WebSocket: ws://localhost/ws/echo (쿼리 파라미터 `token` 필요)
  - 게임 WebSocket: ws://localhost/ws/game?roomId=<매칭된-방>&token=<JWT>
  - 관전 WebSocket: ws://localhost/ws/game?roomId=<진행중-방>&token=<JWT>&role=spectator
  - 소셜 WebSocket: ws://localhost/ws/social?token=<JWT>
  - 채팅 WebSocket: ws://localhost/ws/chat?token=<JWT>
  - 토너먼트 WebSocket: ws://localhost/ws/tournament?token=<JWT>
  - 잡 WebSocket: ws://localhost/ws/jobs?token=<JWT> (job.progress/completed/failed 수신)
  - Swagger (직접 접근): http://localhost:8080/swagger-ui

## 6. 개별 서비스 로컬 실행 (선택)
### 6.1 백엔드
```bash
cd backend
./gradlew bootRun
```
- Redis/DB가 로컬에서 접근 가능해야 하며, `JOB_QUEUE_ENABLED=false`로 설정하면 잡 스트림 소비를 끌 수 있다.

### 6.2 프런트엔드
```bash
cd frontend
npm install
npm run dev -- --host --port 5173
```

### 6.3 워커 (Docker 없이 로컬 실행)
```bash
cd worker
pip install -r requirements.txt
export REDIS_HOST=localhost
export JOB_QUEUE_REQUEST_STREAM=job.requests
export JOB_QUEUE_PROGRESS_STREAM=job.progress
export JOB_QUEUE_RESULT_STREAM=job.results
export JOB_QUEUE_CONSUMER_GROUP=replay-jobs
export WORKER_ID=local-worker
python main.py
```
- ffmpeg CLI가 PATH에 있어야 하며, 입력/출력 경로(`/data/replays` 또는 `backend`와 동일한 `REPLAY_STORAGE_PATH`)를 공유해야 한다.

## 7. 테스트 실행
### 7.1 백엔드 테스트
```bash
cd backend
./gradlew test
```
- Testcontainers로 Redis를 자동 기동해 잡 플로우 통합 테스트(`JobFlowTest`)를 수행한다.

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

### 7.4 프런트엔드 성능 캡처 (리플로우 검증)
```bash
cd frontend
npm install
# Playwright 브라우저 설치가 네트워크로 실패할 수 있음 (샌드박스에서는 ERR_SOCKET_CLOSED 경고 가능)
npx playwright install chromium || true
node scripts/captureReflowTrace.js
```
- dev 서버는 8000 포트(`npm run dev -- --host --port 8000 --strictPort`)에서 실행해야 `captureReflowTrace`가 정상 접근한다.
- 샌드박스에서 Chromium 다운로드가 막힐 경우 `browser_container.run_playwright_script`로 동일 스크립트를 실행하고, 생성된 `reports/performance/v0.14.0/*.zip`(로컬 생성물)을 DevTools로 확인한다. 저장소에는 바이너리 ZIP을 커밋하지 않는다.

### 7.5 워커 테스트
```bash
pip install -r worker/requirements.txt
REQUIRE_FFMPEG=1 python -m unittest discover -s worker -p "test_*.py"
```
- ffmpeg/ffprobe가 PATH에 없으면 실패하며, 테스트는 임시 디렉터리에서 MP4/PNG를 생성한 뒤 자동 정리한다.

## 8. 버전별 메모 (v0.14.0)
- 주요 기능: 리더보드/작업 목록 페이징 및 가상화, 잡 진행률 WebSocket 재시도 정책(3회 제한, 백오프), UTF-8 회귀 테스트 추가.
- 성능 캡처: 7.4 절차로 `reports/performance/v0.14.0/` 경로에 로컬 생성하며, 바이너리 ZIP은 저장소에 포함하지 않는다(필요 시 직접 생성 후 DevTools에서 검토).
- UTF-8 보증: `backend/src/test/java/com/codexpong/backend/Utf8RegressionTest.java`가 REST/DB/WebSocket 경로를 통합 검증하므로, 인코딩 설정 변경 시 반드시 재실행한다.
