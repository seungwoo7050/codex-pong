import { FormEvent, useEffect, useState } from 'react'
import { API_BASE_URL } from '../constants'

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
 *   - v0.1.0에서는 인증 없이 입력 폼과 결과 목록만 제공한다.
 * 버전: v0.1.0
 * 관련 설계문서:
 *   - design/frontend/v0.1.0-core-layout-and-routing.md
 * 변경 이력:
 *   - v0.1.0: 경기 생성/조회 UI 추가
 */
export function LobbyPage() {
  const [playerA, setPlayerA] = useState('플레이어A')
  const [playerB, setPlayerB] = useState('플레이어B')
  const [scoreA, setScoreA] = useState(5)
  const [scoreB, setScoreB] = useState(3)
  const [results, setResults] = useState<GameResult[]>([])
  const [loading, setLoading] = useState(false)

  const loadResults = async () => {
    const response = await fetch(`${API_BASE_URL}/api/games`)
    const data = await response.json()
    setResults(data)
  }

  useEffect(() => {
    loadResults()
  }, [])

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    setLoading(true)
    try {
      await fetch(`${API_BASE_URL}/api/games`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ playerA, playerB, scoreA, scoreB }),
      })
      await loadResults()
    } finally {
      setLoading(false)
    }
  }

  return (
    <main className="page">
      <section className="panel">
        <h2>테스트 경기 생성</h2>
        <p>두 플레이어와 점수를 입력하고 더미 결과를 저장합니다.</p>
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
