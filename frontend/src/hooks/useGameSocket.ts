import { useEffect, useRef, useState } from 'react'
import { WS_BASE_URL } from '../constants'
import { GameServerMessage, GameSnapshot } from '../shared/types/game'

/**
 * [훅] frontend/src/hooks/useGameSocket.ts
 * 설명:
 *   - 주어진 roomId와 토큰으로 게임 WebSocket을 연결하고 상태 스냅샷을 관리한다.
 *   - 입력 방향을 서버에 전송하는 헬퍼를 제공한다.
 * 버전: v0.3.0
 * 관련 설계문서:
 *   - design/frontend/v0.3.0-game-lobby-and-play-ui.md
 *   - design/realtime/v0.3.0-game-loop-and-events.md
 */
export function useGameSocket(roomId?: string | null, token?: string | null) {
  const [connected, setConnected] = useState(false)
  const [error, setError] = useState('')
  const [snapshot, setSnapshot] = useState<GameSnapshot | null>(null)
  const socketRef = useRef<WebSocket | null>(null)

  useEffect(() => {
    if (!roomId || !token) return

    const socket = new WebSocket(
      `${WS_BASE_URL}/ws/game?roomId=${encodeURIComponent(roomId)}&token=${encodeURIComponent(token)}`,
    )
    socketRef.current = socket

    socket.onopen = () => {
      setConnected(true)
      setError('')
    }
    socket.onclose = () => setConnected(false)
    socket.onerror = () => setError('실시간 연결에 실패했습니다.')
    socket.onmessage = (event) => {
      const data: GameServerMessage = JSON.parse(event.data)
      setSnapshot(data.snapshot)
    }

    return () => {
      socket.close()
    }
  }, [roomId, token])

  const sendInput = (direction: 'UP' | 'DOWN' | 'STAY') => {
    if (!socketRef.current || socketRef.current.readyState !== WebSocket.OPEN || !roomId) return
    const payload = {
      type: 'INPUT',
      roomId,
      direction,
    }
    socketRef.current.send(JSON.stringify(payload))
  }

  return { connected, error, snapshot, sendInput }
}
