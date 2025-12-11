import { Link, Route, Routes } from 'react-router-dom'
import { LandingPage } from './pages/LandingPage'
import { LobbyPage } from './pages/LobbyPage'
import { GamePage } from './pages/GamePage'
import { LoginPage } from './pages/LoginPage'
import { RegisterPage } from './pages/RegisterPage'
import { ProfilePage } from './pages/ProfilePage'
import { useAuth } from './features/auth/AuthProvider'
import { ProtectedRoute } from './features/auth/ProtectedRoute'

/**
 * [루트] frontend/src/App.tsx
 * 설명:
 *   - 기본 네비게이션과 페이지 라우팅을 설정한다.
 *   - v0.2.0에서는 로그인/회원가입/프로필 화면과 보호된 라우트를 포함한다.
 * 버전: v0.2.0
 * 관련 설계문서:
 *   - design/frontend/v0.2.0-auth-and-profile-ui.md
 * 변경 이력:
 *   - v0.1.0: React Router 기반 기본 라우팅 추가
 *   - v0.2.0: 인증 라우팅 및 네비게이션 확장
 */
function App() {
  const { user, status, logout } = useAuth()

  return (
    <div className="app-shell">
      <header className="header">
        <Link to="/" className="brand">
          Codex Pong
        </Link>
        <nav className="nav">
          <Link to="/lobby">로비</Link>
          <Link to="/game">게임</Link>
          {status === 'authenticated' ? (
            <>
              <Link to="/profile">내 프로필</Link>
              <button className="link-button" type="button" onClick={logout}>
                로그아웃
              </button>
              <span className="nickname">{user?.nickname}</span>
            </>
          ) : (
            <>
              <Link to="/login">로그인</Link>
              <Link to="/register">회원가입</Link>
            </>
          )}
        </nav>
      </header>
      <Routes>
        <Route path="/" element={<LandingPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route
          path="/lobby"
          element={(
            <ProtectedRoute>
              <LobbyPage />
            </ProtectedRoute>
          )}
        />
        <Route
          path="/game"
          element={(
            <ProtectedRoute>
              <GamePage />
            </ProtectedRoute>
          )}
        />
        <Route
          path="/profile"
          element={(
            <ProtectedRoute>
              <ProfilePage />
            </ProtectedRoute>
          )}
        />
      </Routes>
    </div>
  )
}

export default App
