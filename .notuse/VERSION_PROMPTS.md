# VERSION_PROMPTS.md

이 문서는 트센 전용 레포에서 **버전별로 에이전트에게 그대로 던질 수 있는 프롬프트 모음**이다.  
각 버전 작업을 시작할 때, 해당 섹션의 프롬프트를 통째로 복사해서 에이전트에게 지시하면 된다.

공통 전제:
- 레포 루트에는 이미 다음 문서가 존재한다고 가정한다:
  - `AGENTS.md`
  - `STACK_DESIGN.md`
  - `PRODUCT_SPEC.md`
  - `CODING_GUIDE.md`
  - `DOC_TEMPLATES.md`
  - `VERSIONING.md`
- 모든 주석/문서는 **한국어로 작성**되어야 한다(AGENTS.md 규칙 준수).

---

## v0.1.0 – Core skeleton & minimal vertical slice

```text
레포 루트의 AGENTS.md, STACK_DESIGN.md, PRODUCT_SPEC.md, CODING_GUIDE.md,
DOC_TEMPLATES.md, VERSIONING.md를 모두 먼저 읽어라.

VERSIONING.md에 정의된 v0.1.0 범위(코어 스켈레톤 + 최소 수직 플로우)만을 대상으로 작업해라.
AGENTS.md에 정의된 개발 루프를 그대로 따라라.

구체적으로 v0.1.0에서 해야 할 일은 다음과 같다:
- backend/, frontend/, infra/ 디렉터리 구조를 생성하고, Spring Boot + React + Docker 기반 스켈레톤을 만든다.
- 백엔드에 헬스체크 및 테스트용 간단 API를 추가한다.
- 프론트엔드에 랜딩 페이지, 테스트용 로비/게임 화면의 최소 UI를 만든다.
- WebSocket 엔드포인트에 대한 기본 연결/에코 수준의 와이어링을 만든다.
- Docker Compose로 backend / frontend / db(MariaDB) / redis(옵션) / nginx를 올릴 수 있게 구성한다.

그 다음, AGENTS.md의 규칙에 따라:
- v0.1.0에 필요한 코드 및 테스트를 작성하고 모두 통과시키고,
- design/backend, design/frontend, design/realtime, design/infra 아래에
  v0.1.0 관련 설계 문서를 한국어로 작성하며,
- CLONE_GUIDE.md를 v0.1.0 기준으로 생성/작성하고,
- VERSIONING.md에서 v0.1.0 상태를 완료로 표시해라.

v0.1.0 범위를 넘어서는 기능(계정 시스템, 랭크, 친구/채팅 등)은 구현하지 마라.
모든 주석과 문서는 한국어로 작성해야 한다.
```

---

## v0.2.0 – Accounts, authentication & basic profile

```text
AGENTS.md, STACK_DESIGN.md, PRODUCT_SPEC.md, CODING_GUIDE.md,
DOC_TEMPLATES.md, VERSIONING.md를 다시 읽고 현재 코드/문서 상태를 파악해라.

이번 작업의 대상은 VERSIONING.md에 정의된 v0.2.0 (계정/인증/기본 프로필)만이다.
v0.2.0 범위 밖의 기능은 건드리지 마라.

AGENTS.md에 정의된 개발 루프를 따라 다음을 수행해라:
- 백엔드:
  - User 엔티티/리포지토리를 추가하고,
  - 회원가입/로그인/로그아웃 API를 구현한다.
  - 세션 또는 JWT 방식 중 STACK_DESIGN.md/설계문서에 합치되는 방식을 사용한다.
  - 기본 프로필(닉네임, 아바타, 생성/수정 시각)을 처리한다.
- 프론트엔드:
  - 회원가입/로그인 화면과 "내 프로필" 화면을 구현한다.
  - 인증 상태 관리(로그인 유지, 보호된 라우트)를 구현한다.
- 실시간:
  - WebSocket 연결 시 인증된 사용자와 연결을 연동할 수 있는 최소 구조를 준비한다.
- 인프라:
  - User/Auth 관련 DB 스키마를 적용하고,
  - 필요한 환경변수(예: DB 계정, 시크릿 키)를 CLONE_GUIDE.md에 반영한다.

이후:
- v0.2.0 범위에 대한 테스트(백엔드/프론트엔드)를 추가/수정하고 모두 통과시킨 뒤,
- design/backend/v0.2.0-auth-and-profile.md,
  design/frontend/v0.2.0-auth-and-profile-ui.md 등을 한국어로 작성/업데이트하고,
- CLONE_GUIDE.md를 v0.2.0 기준으로 업데이트하며,
- VERSIONING.md에서 v0.2.0 상태를 완료로 표시해라.

모든 주석과 문서는 한국어로 작성해야 한다.
```

---

## v0.3.0 – Real-time 1v1 game & simple matchmaking

```text
AGENTS.md, STACK_DESIGN.md, PRODUCT_SPEC.md, CODING_GUIDE.md,
DOC_TEMPLATES.md, VERSIONING.md를 읽고 현재 v0.2.0까지의 구현 상태를 파악해라.

이번 작업의 대상은 VERSIONING.md에 정의된 v0.3.0 (실시간 1:1 게임 + 단순 매칭)만이다.
다른 버전의 기능은 추가하거나 변경하지 마라.

AGENTS.md의 개발 루프를 따라 다음을 구현해라:
- 백엔드:
  - 게임 룸 관리(생성/삭제, 참가자 관리)를 위한 도메인/서비스를 구현한다.
  - 실시간 게임 엔진(틱 기반 루프, 공/패들/점수)을 구현한다.
  - 간단한 "빠른 대전" 큐를 통해 두 유저를 매칭하는 매칭 로직을 구현한다.
  - 게임 결과를 User와 연결해 DB에 저장한다.
- 프론트엔드:
  - 로비 화면에서 "빠른 대전" 버튼과 매칭 진행 상태 UI를 구현한다.
  - 게임 화면에서 백엔드로부터 받은 상태를 기반으로 패들/공/점수를 렌더링한다.
- 실시간:
  - WebSocket 이벤트를 정의/구현한다:
    - 클라이언트 → 서버: 입력/준비 상태 등
    - 서버 → 클라이언트: 게임 상태 업데이트, 시작/종료 신호 등
  - 이벤트 계약을 design/realtime 문서에 정리한다.
- 인프라:
  - WebSocket 관련 타임아웃/리버스 프록시 설정을 적절히 조정한다.

이후:
- 게임 엔진과 매칭 흐름에 대한 테스트를 작성/수정하여 모두 통과시키고,
- design/backend/v0.3.0-game-and-matchmaking.md,
  design/frontend/v0.3.0-game-lobby-and-play-ui.md,
  design/realtime/v0.3.0-game-loop-and-events.md를 한국어로 작성/업데이트하고,
- CLONE_GUIDE.md에 v0.3.0 관점에서 변경된 실행/테스트 방법이 있으면 반영하고,
- VERSIONING.md에서 v0.3.0을 완료로 표시해라.

모든 주석과 문서는 한국어로 작성해야 한다.
```

---

## v0.4.0 – Ranked mode & basic ranking system

```text
AGENTS.md, STACK_DESIGN.md, PRODUCT_SPEC.md, CODING_GUIDE.md,
DOC_TEMPLATES.md, VERSIONING.md를 읽어라.

이번 작업의 대상은 VERSIONING.md에 정의된 v0.4.0 (랭크 큐 + 기본 랭킹 시스템)만이다.
v0.4.0에서 요구하지 않는 소셜/채팅/토너먼트 기능은 수정하지 마라.

AGENTS.md의 개발 루프를 따라 다음을 구현해라:
- 백엔드:
  - User에 레이팅 필드를 추가하고,
  - 랭크 큐(일반 큐와 분리)를 구현한다.
  - ELO/MMR 스타일의 레이팅 갱신 로직을 구현한다.
  - 간단한 리더보드 조회 API를 추가한다.
- 프론트엔드:
  - 랭크 매치 진입 UI(랭크/일반 구분)를 구현한다.
  - 프로필/로비 화면에 현재 레이팅/티어를 표시한다.
  - 기본 리더보드 화면을 만든다.
- 실시간:
  - 랭크 매치와 일반 매치를 구분할 수 있도록 이벤트/저장 구조를 조정한다.

이후:
- 랭크 매치/레이팅 변화/리더보드에 관한 테스트를 추가/수정해 모두 통과시키고,
- design/backend/v0.4.0-ranking-system.md,
  design/frontend/v0.4.0-ranking-and-leaderboard-ui.md를 한국어로 작성/업데이트하고,
- CLONE_GUIDE.md에 v0.4.0에서 추가/변경된 요소가 있다면 반영하고,
- VERSIONING.md에서 v0.4.0 상태를 완료로 표시해라.

모든 주석과 문서는 한국어로 작성해야 한다.
```

---

## v0.5.0 – Friends & invitations (basic social graph)

```text
AGENTS.md, STACK_DESIGN.md, PRODUCT_SPEC.md, CODING_GUIDE.md,
DOC_TEMPLATES.md, VERSIONING.md를 읽어라.

이번 작업의 대상은 VERSIONING.md에 정의된 v0.5.0 (친구/차단/초대)만이다.
랭크/채팅/토너먼트/관전 등 다른 도메인의 기능 스펙은 건드리지 마라.

AGENTS.md의 개발 루프를 따라 다음을 구현해라:
- 백엔드:
  - 친구 관계(요청/수락/거절) 도메인/엔티티/서비스를 구현한다.
  - 차단 목록(블랙리스트)을 관리하는 기능을 추가한다.
  - 친구에게 게임 초대를 보내고 수락 시 매치가 시작되도록 API/로직을 구현한다.
- 프론트엔드:
  - 친구 목록 UI(온라인/오프라인 상태 포함)를 구현한다.
  - 친구 요청 수락/거절, 차단/차단 해제 UI를 구현한다.
  - 친구 초대 버튼 및 초대 수락/거절 플로우를 만든다.
- 실시간(선택/버전에 따라):
  - 친구 관련 이벤트(요청/수락/초대)를 WebSocket으로 푸시할 수 있는 구조를 추가한다.

이후:
- 친구/차단/초대 기능에 대한 테스트를 추가/수정해 모두 통과시키고,
- design/backend/v0.5.0-friends-and-blocks.md,
  design/frontend/v0.5.0-friends-and-invites-ui.md를 한국어로 작성/업데이트하고,
- CLONE_GUIDE.md에 필요한 변경사항을 반영하고,
- VERSIONING.md에서 v0.5.0 상태를 완료로 표시해라.

모든 주석과 문서는 한국어로 작성해야 한다.
```

---

## v0.6.0 – Chat (DM, lobby chat, match chat)

```text
AGENTS.md, STACK_DESIGN.md, PRODUCT_SPEC.md, CODING_GUIDE.md,
DOC_TEMPLATES.md, VERSIONING.md를 읽어라.

이번 작업의 대상은 VERSIONING.md에 정의된 v0.6.0 (DM, 로비 채팅, 매치 채팅)만이다.
다른 버전 목표(토너먼트/관전 등)는 건드리지 마라.

AGENTS.md의 개발 루프를 따라 다음을 구현해라:
- 백엔드:
  - 1:1 DM 메시지 저장/조회 API 및 도메인을 구현한다.
  - 로비/글로벌 채팅 채널을 관리하는 구조를 만든다.
  - 매치 룸에 연결된 채팅(게임 중 채팅)을 구현한다.
  - 기본 뮤트 처리 등 최소한의 채팅 제재 훅을 준비한다.
- 프론트엔드:
  - 친구 리스트와 통합된 DM UI를 구현한다.
  - 로비 채팅 패널을 구현한다.
  - 게임 화면에서 사용할 매치 채팅 UI를 구현한다.
- 실시간:
  - 채팅용 WebSocket 이벤트(전송/수신)를 정의/구현한다.
  - design/realtime에 채팅 이벤트 스펙을 정리한다.

이후:
- DM/로비/매치 채팅 플로우에 대한 테스트를 추가/수정해 모두 통과시키고,
- design/backend/v0.6.0-chat-and-channels.md,
  design/frontend/v0.6.0-chat-ui.md,
  design/realtime/v0.6.0-chat-events.md를 한국어로 작성/업데이트하고,
- CLONE_GUIDE.md에 필요한 변경사항을 반영하고,
- VERSIONING.md에서 v0.6.0 상태를 완료로 표시해라.

모든 주석과 문서는 한국어로 작성해야 한다.
```

---

## v0.7.0 – Tournaments & events (simple bracket)

```text
AGENTS.md, STACK_DESIGN.md, PRODUCT_SPEC.md, CODING_GUIDE.md,
DOC_TEMPLATES.md, VERSIONING.md를 읽어라.

이번 작업의 대상은 VERSIONING.md에 정의된 v0.7.0 (단순 토너먼트/이벤트)만이다.
다른 도메인의 기능 스펙은 v0.7.0 범위를 넘어 변경하지 마라.

AGENTS.md의 개발 루프를 따라 다음을 구현해라:
- 백엔드:
  - 토너먼트 엔티티 및 관련 도메인(참가자, 라운드/매치, 진행 상태)을 정의한다.
  - 토너먼트 생성/참여/시작/진행/완료 플로우를 구현한다.
  - 기존 게임/매칭 시스템과 연동하여 토너먼트 매치가 정상적으로 열리도록 한다.
- 프론트엔드:
  - 토너먼트 생성/참여 UI를 구현한다.
  - 간단한 브래킷 화면을 구현해서 매치 진행 상황을 보여준다.
- 실시간:
  - 토너먼트 관련 알림(매치 준비, 다음 라운드 시작 등)을 WebSocket 이벤트로 구현한다.

이후:
- 토너먼트 생성/참여/진행 플로우에 대한 테스트를 추가/수정해 모두 통과시키고,
- design/backend/v0.7.0-tournaments.md,
  design/frontend/v0.7.0-tournament-ui.md,
  design/realtime/v0.7.0-tournament-events.md를 한국어로 작성/업데이트하고,
- CLONE_GUIDE.md에 필요한 변경사항을 반영하고,
- VERSIONING.md에서 v0.7.0 상태를 완료로 표시해라.

모든 주석과 문서는 한국어로 작성해야 한다.
```

---

## v0.8.0 – Spectator mode & live viewing

```text
AGENTS.md, STACK_DESIGN.md, PRODUCT_SPEC.md, CODING_GUIDE.md,
DOC_TEMPLATES.md, VERSIONING.md를 읽어라.

이번 작업의 대상은 VERSIONING.md에 정의된 v0.8.0 (관전 모드/라이브 보기)만이다.

AGENTS.md의 개발 루프를 따라 다음을 구현해라:
- 백엔드:
  - 진행 중인 매치에 관전자로 참여할 수 있는 구조를 추가한다.
  - 관전자 수 제한, 지연 전송 등 필요한 정책을 설계/구현한다.
  - 게임 상태를 관전자에게도 브로드캐스트할 수 있도록 이벤트를 확장한다.
- 프론트엔드:
  - 관전 전용 보기 화면을 구현한다.
  - 친구 목록/리더보드/별도 목록에서 관전 진입이 가능하도록 진입점을 만든다.
- 실시간:
  - 플레이어와 관전자를 구분할 수 있는 이벤트/채널 설계를 적용한다.

이후:
- 관전 진입/종료/시청 플로우에 대한 테스트를 가능한 범위에서 추가/수정해 모두 통과시키고,
- design/backend/v0.8.0-spectator-mode.md,
  design/frontend/v0.8.0-spectator-ui.md,
  design/realtime/v0.8.0-spectator-events.md를 한국어로 작성/업데이트하고,
- CLONE_GUIDE.md에 변경사항이 있다면 반영하고,
- VERSIONING.md에서 v0.8.0 상태를 완료로 표시해라.

모든 주석과 문서는 한국어로 작성해야 한다.
```

---

## v0.9.0 – Admin/ops tooling & monitoring stack

```text
AGENTS.md, STACK_DESIGN.md, PRODUCT_SPEC.md, CODING_GUIDE.md,
DOC_TEMPLATES.md, VERSIONING.md를 읽어라.

이번 작업의 대상은 VERSIONING.md에 정의된 v0.9.0 (관리/운영 도구 + 모니터링 스택)만이다.

AGENTS.md의 개발 루프를 따라 다음을 구현해라:
- 백엔드:
  - 관리자용 API를 구현한다:
    - 유저/매치 기록 조회
    - 밴/정지/뮤트 처리
    - 간단한 통계/상태 조회
  - 구조화된 로그(JSON 등)를 남기도록 로깅을 정리한다.
- 프론트엔드:
  - 최소한의 관리자 콘솔 UI를 구현한다(웹에서 기본 제재/조회 작업 가능 수준).
- 인프라:
  - Prometheus + Grafana(또는 동급)의 모니터링 스택을 붙이고,
  - 주요 메트릭(요청 수, 에러 비율, 활성 게임/유저 수 등)을 대시보드로 노출한다.
  - Nginx/백엔드 로그가 분석 가능하도록 포맷을 정리한다.

이후:
- 어드민/모니터링 관련 기능에 대한 테스트(가능한 범위)를 추가/수정하고,
- design/backend/v0.9.0-admin-and-ops.md,
  design/infra/v0.9.0-monitoring-stack.md를 한국어로 작성/업데이트하고,
- CLONE_GUIDE.md에 모니터링 스택 실행/접근 방법을 추가하고,
- VERSIONING.md에서 v0.9.0 상태를 완료로 표시해라.

모든 주석과 문서는 한국어로 작성해야 한다.
```

---

## v0.10.0 – Korean-market refinements

```text
AGENTS.md, STACK_DESIGN.md, PRODUCT_SPEC.md, CODING_GUIDE.md,
DOC_TEMPLATES.md, VERSIONING.md를 읽어라.

이번 작업의 대상은 VERSIONING.md에 정의된 v0.10.0 (한국 시장 맞춤 보완)만이다.

AGENTS.md의 개발 루프를 따라 다음을 구현해라:
- 백엔드:
  - (VERSIONING/PRODUCT_SPEC에 정의된 경우) Kakao/Naver OAuth 로그인 플로우를 추가한다.
  - KST/로케일 관련 설정을 정리하고, API에서 사용하는 타임스탬프 포맷을 명확히 한다.
- 프론트엔드:
  - 주요 UI 텍스트를 한국어 기준으로 확정한다(임시/혼용 상태 제거).
  - 한글 렌더링/폰트/레이아웃을 고려해 화면을 다듬는다.
- 인프라:
  - 모든 컨테이너와 DB가 `TZ=Asia/Seoul`, `utf8mb4`로 제대로 동작하는지 확인/보완한다.
  - 한글/이모지 데이터가 깨지지 않는지 점검하고 필요 시 설정을 수정한다.

이후:
- 한글/타임존/로그인 관련 플로우에 대한 테스트를 추가/수정해 모두 통과시키고,
- design/backend/v0.10.0-kor-auth-and-locale.md,
  design/frontend/v0.10.0-kor-ux.md,
  design/infra/v0.10.0-kst-and-utf8mb4.md를 한국어로 작성/업데이트하고,
- CLONE_GUIDE.md에 한글/타임존/로컬라이제이션 관련 유의사항을 반영하고,
- VERSIONING.md에서 v0.10.0 상태를 완료로 표시해라.

모든 주석과 문서는 한국어로 작성해야 한다.
```

---

## v0.11.0 – Match replay recording & replay browser (MVP)

```text
레포 루트의 AGENTS.md, STACK_DESIGN.md, PRODUCT_SPEC.md, CODING_GUIDE.md,
DOC_TEMPLATES.md, VERSIONING.md를 모두 먼저 읽어라.

이번 작업의 대상은 VERSIONING.md에 정의될 v0.11.0 (리플레이 녹화/브라우저/뷰어)만이다.
v0.11.0 범위 밖의 기능은 추가하거나 변경하지 마라.
(특히 인증/매칭/게임 룸/랭크/친구/채팅/토너먼트/관전/어드민의 스펙을 바꾸지 마라.)

AGENTS.md 개발 루프를 따라 다음을 구현해라:

- 백엔드:
  - DB 스키마:
    - replay 테이블을 추가한다:
      - replay_id (PK), match_id (FK), owner_user_id (FK),
        created_at, duration_ms, event_format(JSONL_V1), storage_uri, checksum
  - 녹화(Recording):
    - 매치 종료 시점에 replay 이벤트 스트림을 생성/저장한다.
    - replay 파일은 우선 로컬 영속 볼륨(도커 named volume)에 저장한다.
    - replay 메타데이터는 DB에 저장한다.
  - API:
    - GET /api/replays (내 리플레이 목록, paging/sorting)
    - GET /api/replays/{replayId} (메타데이터 + 접근권한)
    - GET /api/matches/{matchId}/replay (해당 매치의 replay 메타 조회)
    - replay 이벤트 파일 제공 방식은 둘 중 하나로 명시적으로 고정해라:
      - (A) 백엔드에서 streaming 응답
      - (B) 백엔드가 서명 URL(또는 내부 다운로드 토큰)을 제공
  - 권한:
    - owner만 조회 가능(관리자 예외 정책이 있으면 문서에 명시)

- 프론트엔드:
  - “내 리플레이” 화면:
    - 목록(매치 날짜/상대/결과/길이)
    - 상대 닉네임 검색
  - “리플레이 뷰어” 화면:
    - 재생/일시정지
    - 시크(seek)
    - 배속(0.5x/1x/2x)
    - 기존 게임 캔버스 렌더러를 read-only 모드로 재사용

- 실시간:
  - v0.11.0에서는 신규 WebSocket 이벤트를 추가하지 마라(리플레이는 사후 기능).
  - 기존 WS 스택을 깨지 않는지만 회귀 테스트로 확인해라.

- 인프라:
  - Docker Compose에 replay 파일용 named volume을 추가하고 마운트한다.
  - retention 정책을 최소 1개로 고정해라(예: 유저당 N개, 또는 N일).
  - CLONE_GUIDE.md에 replay 저장 경로/볼륨/정리 정책을 명시한다.

이후:
- v0.11.0 범위에 대한 테스트(백엔드/프론트)를 추가/수정해 모두 통과시키고,
- design/backend/v0.11.0-replay-recording-and-storage.md
  design/frontend/v0.11.0-replay-browser-and-viewer.md
  design/infra/v0.11.0-replay-storage-and-retention.md
  를 한국어로 작성/업데이트하고,
- CLONE_GUIDE.md를 v0.11.0 기준으로 업데이트하며,
- VERSIONING.md에서 v0.11.0 상태를 완료로 표시해라.

모든 주석과 문서는 한국어로 작성해야 한다.
```

---

## v0.12.0 – Replay export pipeline via IPC worker (jobs + progress)

```text
AGENTS.md, STACK_DESIGN.md, PRODUCT_SPEC.md, CODING_GUIDE.md,
DOC_TEMPLATES.md, VERSIONING.md를 다시 읽고 현재 코드/문서 상태를 파악해라.

이번 작업의 대상은 VERSIONING.md에 정의될 v0.12.0 (리플레이 내보내기 + 잡/진행률 + IPC 워커)만이다.
v0.12.0 범위 밖의 기능은 건드리지 마라.

AGENTS.md 개발 루프를 따라 다음을 구현해라:

- 백엔드:
  - DB 스키마:
    - job 테이블을 추가한다:
      - job_id(PK), job_type(REPLAY_EXPORT_MP4, REPLAY_THUMBNAIL),
        owner_user_id, target_replay_id, status(QUEUED/RUNNING/SUCCEEDED/FAILED/CANCELLED),
        progress(0..100), created_at/started_at/ended_at, error_code/error_message, result_uri
  - API(명시적으로 고정):
    - POST /api/replays/{replayId}/exports/mp4 -> jobId 반환(즉시 반환)
    - POST /api/replays/{replayId}/exports/thumbnail -> jobId 반환(즉시 반환)
    - GET  /api/jobs/{jobId} -> 상태/진행률
    - GET  /api/jobs -> 내 job 목록(paging/filter)
    - GET  /api/jobs/{jobId}/result -> 완료 시 다운로드
  - Dispatcher:
    - 잡 생성 시 큐로 job 메시지를 발행한다(상관관계 id는 jobId).
    - idempotency 규칙을 문서로 고정해라(같은 jobId는 결과가 중복 생성되면 안 됨).
    - retry/dead-letter 정책을 최소 단위로라도 “명시”해라.

- IPC / Queue(명시적으로 하나 선택해 고정):
  - Redis Streams(권장) 또는 프로젝트에 이미 채택된 큐 1개만 사용.
  - 메시지 스키마를 고정:
    - request: { jobId, jobType, replayId, options }
    - progress: { jobId, progress, phase, message }
    - result: { jobId, status, resultUri, checksum, errorCode, errorMessage }

- 워커(신규 서비스):
  - Docker Compose에 replay-worker 서비스를 추가한다(백엔드와 별도 프로세스).
  - 큐 메시지를 소비해 실제 export 작업을 수행하고 progress/result를 발행한다.
  - 워커 장애가 백엔드를 죽이지 않아야 한다(격리 + 재시도/실패 처리 일관성).

- 실시간:
  - WebSocket 이벤트를 명시적으로 정의/구현:
    - job.progress (jobId, progress, phase, message)
    - job.completed (jobId, resultUri)
    - job.failed (jobId, errorCode, errorMessage)
  - 백엔드가 워커 progress를 받아 유저에게 push 하도록 구현한다.

- 프론트엔드:
  - 리플레이 뷰어에 “MP4 내보내기”, “썸네일 생성” 버튼 추가
  - job drawer(또는 모달):
    - 진행률 바, 로그, 완료 시 다운로드 링크
  - jobs 목록 화면:
    - 상태 필터/정렬, 실패 이유 확인

이후:
- job/worker/WS 흐름에 대한 테스트를 추가/수정해 모두 통과시키고,
- design/backend/v0.12.0-jobs-api-and-state-machine.md
  design/realtime/v0.12.0-job-progress-events.md
  design/infra/v0.12.0-worker-and-queue-topology.md
  를 한국어로 작성/업데이트하고,
- CLONE_GUIDE.md에 worker/redis/큐 토폴로지, 실행 방법을 반영하고,
- VERSIONING.md에서 v0.12.0 상태를 완료로 표시해라.

모든 주석과 문서는 한국어로 작성해야 한다.
```

---

## v0.13.0 – GPU-accelerated replay rendering + optional HW-encode export

```text
AGENTS.md, STACK_DESIGN.md, PRODUCT_SPEC.md, CODING_GUIDE.md,
DOC_TEMPLATES.md, VERSIONING.md를 읽고 현재 상태를 파악해라.

이번 작업의 대상은 VERSIONING.md에 정의될 v0.13.0 (GPU 가속 경로 + CPU fallback)만이다.
새로운 대형 도메인(친구/채팅/토너먼트 등)을 추가하지 마라.

AGENTS.md 개발 루프를 따라 다음을 구현해라:

- 프론트엔드(클라이언트 GPU):
  - 리플레이 뷰어 렌더러를 2중 경로로 만든다:
    - WebGL 경로(가능하면 기본)
    - Canvas2D fallback(WebGL 미지원/실패 시)
  - 런타임에서 WebGL 가능 여부를 감지하고 fallback이 “자동”이어야 한다.
  - 개발 모드에서 최소 성능 계측을 남긴다:
    - 1x/2x 재생 시 평균 FPS(로그 또는 dev UI)

- 워커(서버 HW encode):
  - export MP4에 대해 옵션 플래그를 추가한다:
    - EXPORT_HW_ACCEL=true/false (환경변수 또는 설정)
  - 워커 시작 시 하드웨어 인코더/디코더 지원 여부를 감지하고 로그로 남긴다.
  - 미지원이면 자동으로 소프트웨어 경로로 fallback 한다.
  - 결과물은 “재생 가능”해야 하고 실패 시 error_code/error_message가 명확해야 한다.

- 인프라:
  - GPU는 “선택”이다:
    - CI는 CPU-only 유지
    - 로컬/특정 환경에서만 GPU 경로를 검증할 수 있도록 문서에 체크리스트를 만든다.

이후:
- WebGL/Canvas2D 두 경로 모두 동작하는 회귀 테스트(최소 스모크)를 추가하고,
- HW encode on/off 둘 다(가능한 환경에서) 동작을 확인한 뒤 문서로 남기고,
- design/frontend/v0.13.0-webgl-replay-renderer.md
  design/infra/v0.13.0-optional-gpu-runtime.md
  design/backend/v0.13.0-export-hw-accel-flags.md
  를 한국어로 작성/업데이트하고,
- CLONE_GUIDE.md에 GPU 옵션 실행 방법을 반영하고,
- VERSIONING.md에서 v0.13.0 상태를 완료로 표시해라.

모든 주석과 문서는 한국어로 작성해야 한다.
```

---

## v0.14.0 – Frontend reflow audit + async patterns hardening (portfolio-grade)

```text
AGENTS.md, STACK_DESIGN.md, PRODUCT_SPEC.md, CODING_GUIDE.md,
DOC_TEMPLATES.md, VERSIONING.md를 읽어라.

이번 작업의 대상은 VERSIONING.md에 정의될 v0.14.0 (리플로우 통제 + 비동기 패턴 정리 + UTF-8 회귀)만이다.
새 기능을 늘리지 말고, “증명 가능한 품질”을 만든다.

AGENTS.md 개발 루프를 따라 다음을 수행해라:

- 프론트엔드(리플로우/성능):
  - 리플로우 감사(audit) 대상 화면을 최소 2개 “명시적으로 선택”하고 문서에 박아라:
    - 예: leaderboard / chat / replays list / jobs list 중 2개 이상
  - 각 화면에 대해:
    - 긴 리스트는 virtualization 또는 paging을 적용한다.
    - layout thrashing 패턴(루프 내 DOM read/write 섞기)을 제거한다.
  - DevTools Performance 캡처로 before/after 근거를 남긴다(문서에 첨부).

- 프론트엔드(비동기 패턴):
  - export/job 진행률 수신 플로우에서 중첩 콜백이 남아있으면 제거하고,
    async/await 또는 Promise 기반으로 공통 래퍼를 만든다.
  - WebSocket 재연결/에러 처리 정책을 “코드 + 문서”로 고정한다.

- 백엔드(UTF-8 회귀):
  - v0.10.0에서 요구한 `utf8mb4 end-to-end`를 테스트로 고정한다.
  - 회귀 테스트에 반드시 포함:
    - 한글 + 이모지 닉네임
    - REST payload
    - WebSocket payload
    - DB round-trip

이후:
- 퍼포먼스 리포트를 한국어로 작성하고(근거 캡처 포함),
- design/frontend/v0.14.0-reflow-audit-and-fixes.md
  design/realtime/v0.14.0-async-ws-client-patterns.md
  design/backend/v0.14.0-utf8-regression-suite.md
  를 한국어로 작성/업데이트하고,
- CLONE_GUIDE.md에 성능 확인 방법(재현 절차)을 반영하고,
- VERSIONING.md에서 v0.14.0 상태를 완료로 표시해라.

모든 주석과 문서는 한국어로 작성해야 한다.
```
---

## v1.0.0 – Portfolio-ready release

```text
AGENTS.md, STACK_DESIGN.md, PRODUCT_SPEC.md, CODING_GUIDE.md,
DOC_TEMPLATES.md, VERSIONING.md를 읽어라.

이번 작업의 대상은 VERSIONING.md에 정의된 v1.0.0 (포트폴리오 공개용 마무리)다.
새로운 대형 기능을 추가하지 말고, 기존 v0.1.0 ~ v0.10.0에서 정의한 범위를
안정화/정리하는 데 집중해라.

AGENTS.md의 개발 루프를 따라 다음을 수행해라:
- 기능 측면:
  - 주요 플로우(회원가입→로그인→게임→전적/랭킹 조회, 친구/채팅/토너먼트/관전)를 실제 사용자가 사용할 수 있는 수준으로 다듬는다.
  - 치명적인 버그, UX적으로 치명적인 문제를 우선적으로 해결한다.
- 코드/테스트:
  - 불필요한 코드/죽은 코드/임시 구현을 정리한다.
  - 핵심 도메인에 대한 테스트 커버리지를 보강한다.
- 문서:
  - 다음 오버뷰 문서를 한국어로 작성/정리한다:
    - design/backend/v1.0.0-overview.md
    - design/frontend/v1.0.0-overview.md
    - design/realtime/v1.0.0-overview.md
    - design/infra/v1.0.0-overview.md
  - CLONE_GUIDE.md를 v1.0.0 기준으로 최신 상태로 유지한다
    (클론 → 로컬/도커 실행 → 테스트 수행까지 전 과정을 설명).
- VERSIONING:
  - VERSIONING.md에서 v1.0.0을 "portfolio-ready"로 명시하고,
    남아 있는 제한사항/미구현 아이템이 있다면 정리해서 기록한다.

마지막으로:
- 모든 주요 경로가 실제로 동작하는지 수동 점검할 수 있도록 준비하고,
- 모든 주석과 문서가 한국어로 정리되어 있는지 다시 한 번 검토해라.
```