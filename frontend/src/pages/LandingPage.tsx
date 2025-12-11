import { Link } from 'react-router-dom'

/**
 * [페이지] frontend/src/pages/LandingPage.tsx
 * 설명:
 *   - 서비스 소개와 빠른 이동 링크를 제공하는 랜딩 화면이다.
 *   - v0.1.0에서는 간단한 안내 문구와 버튼만 배치한다.
 * 버전: v0.1.0
 * 관련 설계문서:
 *   - design/frontend/v0.1.0-core-layout-and-routing.md
 * 변경 이력:
 *   - v0.1.0: 기본 랜딩 섹션 추가
 */
export function LandingPage() {
  return (
    <main className="page">
      <section className="hero">
        <h1>Codex Pong</h1>
        <p>빠르게 연결되는 한국형 실시간 핑퐁 대전을 위한 최소 데모입니다.</p>
        <div className="actions">
          <Link className="button" to="/lobby">
            로비로 이동
          </Link>
          <Link className="secondary" to="/game">
            게임 화면 보기
          </Link>
        </div>
      </section>
    </main>
  )
}
