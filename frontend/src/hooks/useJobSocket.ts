import { useEffect, useRef, useState } from 'react'
import { WS_BASE_URL } from '../constants'

export interface JobSocketEvent {
  type: string
  payload: unknown
}

/**
 * [훅] frontend/src/hooks/useJobSocket.ts
 * 설명:
 *   - v0.12.0 잡 진행률/완료 WebSocket 이벤트를 수신한다.
 *   - 콜백 기반으로 전달해 페이지에서 상태 업데이트를 처리할 수 있도록 한다.
 * 버전: v0.12.0
 * 관련 설계문서:
 *   - design/realtime/v0.12.0-job-progress-events.md
 */
export function useJobSocket(token?: string | null, onEvent?: (event: JobSocketEvent) => void) {
  const [connected, setConnected] = useState(false)
  const [error, setError] = useState('')
  const socketRef = useRef<WebSocket | null>(null)
  const handlerRef = useRef<typeof onEvent>()

  useEffect(() => {
    handlerRef.current = onEvent
  }, [onEvent])

  useEffect(() => {
    if (!token || typeof WebSocket === 'undefined') return () => undefined

    const socket = new WebSocket(`${WS_BASE_URL}/ws/jobs?token=${encodeURIComponent(token)}`)
    socketRef.current = socket

    socket.onopen = () => {
      setConnected(true)
      setError('')
    }
    socket.onclose = () => setConnected(false)
    socket.onerror = () => setError('잡 진행 알림 연결에 실패했습니다.')
    socket.onmessage = (event) => {
      try {
        const parsed = JSON.parse(event.data) as JobSocketEvent
        if (handlerRef.current) {
          handlerRef.current(parsed)
        }
      } catch (e) {
        console.warn('job socket parse error', e)
      }
    }

    return () => {
      socket.close()
    }
  }, [token])

  return { connected, error }
}
