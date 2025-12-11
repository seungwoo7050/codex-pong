import { useEffect, useRef, useState } from 'react'
import { WS_BASE_URL } from '../constants'

/**
 * [훅] frontend/src/hooks/useEchoSocket.ts
 * 설명:
 *   - WebSocket 연결을 맺고 에코 메시지를 주고받는 최소 로직을 제공한다.
 *   - v0.1.0에서 실시간 파이프가 동작하는지 검증하는 용도다.
 * 버전: v0.1.0
 * 관련 설계문서:
 *   - design/realtime/v0.1.0-basic-websocket-wiring.md
 * 변경 이력:
 *   - v0.1.0: 에코 연결/전송/수신 상태 관리 추가
 */
export function useEchoSocket() {
  const [messages, setMessages] = useState<string[]>([])
  const [connected, setConnected] = useState(false)
  const socketRef = useRef<WebSocket | null>(null)

  useEffect(() => {
    const socket = new WebSocket(`${WS_BASE_URL}/ws/echo`)
    socketRef.current = socket

    socket.onopen = () => setConnected(true)
    socket.onclose = () => setConnected(false)
    socket.onmessage = (event) => {
      setMessages((prev) => [...prev, event.data])
    }

    return () => {
      socket.close()
    }
  }, [])

  const sendMessage = (text: string) => {
    if (socketRef.current && connected) {
      socketRef.current.send(text)
    }
  }

  return {
    connected,
    messages,
    sendMessage,
  }
}
