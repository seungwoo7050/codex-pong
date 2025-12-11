import { Link, Route, Routes } from 'react-router-dom'
import { LandingPage } from './pages/LandingPage'
import { LobbyPage } from './pages/LobbyPage'
import { GamePage } from './pages/GamePage'

/**
 * [루트] frontend/src/App.tsx
 * 설명:
 *   - 기본 네비게이션과 페이지 라우팅을 설정한다.
 *   - v0.1.0에서는 랜딩, 로비, 게임 화면만 연결한다.
 * 버전: v0.1.0
 * 관련 설계문서:
 *   - design/frontend/v0.1.0-core-layout-and-routing.md
 * 변경 이력:
 *   - v0.1.0: React Router 기반 기본 라우팅 추가
 */
function App() {
  return (
    <div className="app-shell">
      <header className="header">
        <Link to="/" className="brand">
          Codex Pong
        </Link>
        <nav className="nav">
          <Link to="/lobby">로비</Link>
          <Link to="/game">게임</Link>
        </nav>
      </header>
      <Routes>
        <Route path="/" element={<LandingPage />} />
        <Route path="/lobby" element={<LobbyPage />} />
        <Route path="/game" element={<GamePage />} />
      </Routes>
    </div>
  )
}

export default App
