import { FormEvent, useEffect, useState } from 'react'
import { useAuth } from '../features/auth/AuthProvider'
import { apiFetch } from '../shared/api/client'

interface GameResult {
  id: number
  playerA: string
  playerB: string
  scoreA: number
  scoreB: number
  playedAt: string
}

/**
 * [페이지] frontend/src/pages/LobbyPage.tsx
 * 설명:
 *   - 테스트 경기를 생성하고 최근 결과를 확인하는 간단한 로비 화면이다.
 *   - v0.2.0에서는 인증 토큰을 사용해 보호된 API를 호출한다.
 * 버전: v0.2.0
 * 관련 설계문서:
 *   - design/frontend/v0.2.0-auth-and-profile-ui.md
 * 변경 이력:
 *   - v0.1.0: 경기 생성/조회 UI 추가
 *   - v0.2.0: 인증 연동 및 에러 메시지 추가
 */
export function LobbyPage() {
  const { token, user } = useAuth()
  const [playerA, setPlayerA] = useState('플레이어A')
  const [playerB, setPlayerB] = useState('플레이어B')
  const [scoreA, setScoreA] = useState(5)
  const [scoreB, setScoreB] = useState(3)
  const [results, setResults] = useState<GameResult[]>([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  const loadResults = async () => {
    if (!token) return
    try {
      const data = await apiFetch<GameResult[]>('/api/games', { method: 'GET' }, token)
      setResults(data)
    } catch (err) {
      setError('최근 경기 결과를 불러오지 못했습니다.')
    }
  }

  useEffect(() => {
    loadResults()
  }, [token])

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    setLoading(true)
    setError('')
    try {
      await apiFetch('/api/games', {
        method: 'POST',
        body: JSON.stringify({ playerA, playerB, scoreA, scoreB }),
      }, token)
      await loadResults()
    } catch (err) {
      setError('경기 저장에 실패했습니다.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <main className="page">
      <section className="panel">
        <h2>테스트 경기 생성</h2>
        <p>{user ? `${user.nickname} 님의 토큰으로 결과를 저장합니다.` : '로그인 후 이용 가능합니다.'}</p>
        <form className="form" onSubmit={handleSubmit}>
          <label>
            플레이어 A
            <input value={playerA} onChange={(e) => setPlayerA(e.target.value)} required maxLength={50} />
          </label>
          <label>
            플레이어 B
            <input value={playerB} onChange={(e) => setPlayerB(e.target.value)} required maxLength={50} />
          </label>
          <div className="scores">
            <label>
              A 점수
              <input
                type="number"
                value={scoreA}
                onChange={(e) => setScoreA(Number(e.target.value))}
                min={0}
                max={30}
              />
            </label>
            <label>
              B 점수
              <input
                type="number"
                value={scoreB}
                onChange={(e) => setScoreB(Number(e.target.value))}
                min={0}
                max={30}
              />
            </label>
          </div>
          <button className="button" type="submit" disabled={loading}>
            {loading ? '저장 중...' : '결과 저장'}
          </button>
          {error && <p className="error">{error}</p>}
        </form>
      </section>

      <section className="panel">
        <h2>최근 결과</h2>
        {results.length === 0 && <p>저장된 결과가 없습니다.</p>}
        <ul className="list">
          {results.map((result) => (
            <li key={result.id} className="list-item">
              <div className="row">
                <strong>{result.playerA}</strong>
                <span className="score">{result.scoreA}</span>
              </div>
              <div className="row">
                <strong>{result.playerB}</strong>
                <span className="score">{result.scoreB}</span>
              </div>
              <small>{new Date(result.playedAt).toLocaleString('ko-KR')}</small>
            </li>
          ))}
        </ul>
      </section>
    </main>
  )
}
