import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../features/auth/AuthProvider'
import { useQuickMatch } from '../hooks/useQuickMatch'
import { apiFetch } from '../shared/api/client'

interface GameResult {
  id: number
  playerAId: number
  playerANickname: string
  playerBId: number
  playerBNickname: string
  scoreA: number
  scoreB: number
  roomId: string
  startedAt: string
  finishedAt: string
}

/**
 * [페이지] frontend/src/pages/LobbyPage.tsx
 * 설명:
 *   - v0.3.0 빠른 대전 큐 참여 버튼과 매칭 상태를 표시하는 로비 화면이다.
 *   - 최근 경기 결과를 불러와 게임 흐름이 정상적으로 기록되는지 보여준다.
 * 버전: v0.3.0
 * 관련 설계문서:
 *   - design/frontend/v0.3.0-game-lobby-and-play-ui.md
 */
export function LobbyPage() {
  const { token, user } = useAuth()
  const navigate = useNavigate()
  const { status, roomId, message, start, reset } = useQuickMatch(token)
  const [results, setResults] = useState<GameResult[]>([])
  const [error, setError] = useState('')

  useEffect(() => {
    if (!token) return
    apiFetch<GameResult[]>('/api/games', { method: 'GET' }, token)
      .then(setResults)
      .catch(() => setError('최근 경기 결과를 불러오지 못했습니다.'))
  }, [token])

  useEffect(() => {
    if (status === 'matched' && roomId) {
      navigate(`/game?roomId=${roomId}`)
      reset()
    }
  }, [status, roomId, navigate, reset])

  return (
    <main className="page">
      <section className="panel">
        <h2>빠른 대전</h2>
        <p>{user ? `${user.nickname} 님으로 대전을 시작합니다.` : '로그인 후 이용 가능합니다.'}</p>
        <button className="button" type="button" onClick={start} disabled={status === 'waiting'}>
          {status === 'waiting' ? '상대를 찾는 중...' : '빠른 대전 시작'}
        </button>
        {message && <p className="hint">{message}</p>}
        {error && <p className="error">{error}</p>}
      </section>

      <section className="panel">
        <h2>최근 경기 결과</h2>
        {results.length === 0 && <p>아직 기록된 경기가 없습니다.</p>}
        <ul className="list">
          {results.map((result) => (
            <li key={result.id} className="list-item">
              <div className="row">
                <strong>{result.playerANickname}</strong>
                <span className="score">{result.scoreA}</span>
              </div>
              <div className="row">
                <strong>{result.playerBNickname}</strong>
                <span className="score">{result.scoreB}</span>
              </div>
              <small>{new Date(result.finishedAt).toLocaleString('ko-KR')}</small>
            </li>
          ))}
        </ul>
      </section>
    </main>
  )
}
