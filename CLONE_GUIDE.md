# CLONE_GUIDE (v0.12.0)

## 1. 목적
- v0.12.0 기준 **리플레이 내보내기 잡 파이프라인**까지 포함한 전체 스택을 실행하기 위한 안내서다.
- 기존 실시간 게임/소셜/토너먼트/관전/리플레이 뷰어 흐름에 더해, Redis Streams + 별도 워커로 MP4/썸네일을 생성하고 진행률을 WebSocket으로 확인한다.
- raw WebSocket(`/ws/*`) 전제를 유지한다. 전송 방식 변경 시 `design/realtime/initial-design.md` 및 본 문서를 함께 갱신한다.

## 2. 사전 준비물
- Git
- Docker / Docker Compose
- Node.js 18 (프런트엔드 로컬 실행 시)
- JDK 17 (백엔드 로컬 실행 시)
- ffmpeg CLI (로컬에서 워커를 단독 실행할 때 필요, Docker Compose 사용 시 자동 포함)

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

### 7.4 워커 테스트
```bash
pip install -r worker/requirements.txt
REQUIRE_FFMPEG=1 python -m unittest discover -s worker -p "test_*.py"
```
- ffmpeg/ffprobe가 PATH에 없으면 실패하며, 테스트는 임시 디렉터리에서 MP4/PNG를 생성한 뒤 자동 정리한다.

## 8. 버전별 메모 (v0.12.0)
- 주요 기능: 기존 일반/랭크 대전, 리더보드, 친구/차단/초대, DM/로비/매치 채팅, 단일 제거 토너먼트, 관전 모드, 관리자 API/모니터링, 카카오/네이버 OAuth, KST/utf8mb4 정비, 리플레이 녹화/뷰어 + **잡 기반 리플레이 내보내기(MP4/썸네일) 및 진행률 WebSocket**.
- 리플레이 내보내기 절차: 리플레이 뷰어 → 내보내기 버튼 클릭 → `jobId` 반환 → JobDrawer에서 실시간 진행률 확인 → 완료 후 다운로드. 워커는 JSONL 스냅샷을 직접 렌더링해 실제 경기 흐름이 반영된 MP4/PNG를 생성한다.
- 큐/워커: Redis Streams(`job.requests/progress/results`) + `replay-worker` 컨테이너, ffmpeg CLI 필수. 데드레터 스트림 `job.deadletter`를 점검해 재시도할 수 있다. 출력 경로는 `JOB_EXPORT_PATH` 하위만 허용된다.
