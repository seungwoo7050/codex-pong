#!/usr/bin/env bash
# v0.10.0 유지용 통합 검증 스크립트
set -euo pipefail

export CI=true

( cd backend && [ -f gradle/wrapper/gradle-wrapper.jar ] || gradle wrapper )

( cd backend && ./gradlew test --console=plain --no-daemon )
( cd frontend && npm test )
