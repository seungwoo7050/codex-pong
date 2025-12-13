import { useEffect, useMemo, useRef, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { useAuth } from '../features/auth/AuthProvider'
import { fetchReplayDetail, fetchReplayEvents } from '../features/replay/api'
import { GameCanvas } from '../shared/components/GameCanvas'
import { ReplayDetail, ReplayEventRecord } from '../shared/types/replay'

/**
 * [페이지] frontend/src/pages/ReplayViewerPage.tsx
 * 설명:
 *   - v0.11.0 리플레이 뷰어로, JSONL 이벤트를 시간축에 따라 재생/일시정지/시크/배속 변경 기능을 제공한다.
 *   - 실시간 게임에서 사용한 GameCanvas를 읽기 전용으로 재사용해 동일한 렌더링 결과를 유지한다.
 * 버전: v0.11.0
 * 관련 설계문서:
 *   - design/frontend/v0.11.0-replay-browser-and-viewer.md
 */
export function ReplayViewerPage() {
  const { replayId } = useParams()
  const { token } = useAuth()
  const [detail, setDetail] = useState<ReplayDetail | null>(null)
  const [events, setEvents] = useState<ReplayEventRecord[]>([])
  const [positionMs, setPositionMs] = useState(0)
  const [playing, setPlaying] = useState(false)
  const [speed, setSpeed] = useState(1)
  const [error, setError] = useState('')
  const timerRef = useRef<ReturnType<typeof setInterval> | null>(null)

  const durationMs = detail?.summary.durationMs ?? 0

  useEffect(() => {
    if (!token || !replayId) return
    fetchReplayDetail(Number(replayId), token)
      .then((data) => {
        setDetail(data)
        setError('')
        return fetchReplayEvents(data.downloadPath, token)
      })
      .then((records) => {
        setEvents(records)
        setPositionMs(0)
      })
      .catch(() => setError('리플레이 정보를 불러오는 중 문제가 발생했습니다.'))
  }, [replayId, token])

  useEffect(() => {
    if (playing) {
      timerRef.current = setInterval(() => {
        setPositionMs((prev) => {
          const next = Math.min(durationMs, prev + 50 * speed)
          if (next >= durationMs) {
            setPlaying(false)
            return durationMs
          }
          return next
        })
      }, 50)
    }
    return () => {
      if (timerRef.current) {
        clearInterval(timerRef.current)
        timerRef.current = null
      }
    }
  }, [playing, speed, durationMs])

  const currentSnapshot = useMemo(() => {
    if (events.length === 0) return null
    let latest = events[0].snapshot
    for (const event of events) {
      if (event.offsetMs <= positionMs) {
        latest = event.snapshot
      } else {
        break
      }
    }
    return latest
  }, [events, positionMs])

  const togglePlay = () => {
    if (!events.length) return
    setPlaying((prev) => !prev)
  }

  const handleSeek = (value: number) => {
    setPositionMs(value)
    setPlaying(false)
  }

  return (
    <main className="page">
      <section className="panel">
        <h2>리플레이 뷰어</h2>
        {detail && (
          <p>
            상대: {detail.summary.opponentNickname} / 결과: {detail.summary.myScore} : {detail.summary.opponentScore} /
            길이: {(detail.summary.durationMs / 1000).toFixed(1)}초
          </p>
        )}
        {error && <p className="error">{error}</p>}
        {!currentSnapshot && !error && <p className="hint">이벤트를 불러오는 중...</p>}
        {currentSnapshot && (
          <div className="game-area">
            <GameCanvas snapshot={currentSnapshot} />
            <div className="controls">
              <button type="button" onClick={togglePlay}>
                {playing ? '일시정지' : '재생'}
              </button>
              <label>
                배속
                <select value={speed} onChange={(e) => setSpeed(Number(e.target.value))}>
                  <option value={0.5}>0.5x</option>
                  <option value={1}>1x</option>
                  <option value={2}>2x</option>
                </select>
              </label>
              <input
                type="range"
                min={0}
                max={durationMs}
                value={positionMs}
                onChange={(e) => handleSeek(Number(e.target.value))}
              />
              <span>
                {Math.round(positionMs / 100) / 10}s / {Math.round(durationMs / 100) / 10}s
              </span>
              <Link className="button" to="/replays">
                목록으로
              </Link>
            </div>
          </div>
        )}
      </section>
    </main>
  )
}
