import { FormEvent, useState } from 'react'
import { useEchoSocket } from '../hooks/useEchoSocket'

/**
 * [페이지] frontend/src/pages/GamePage.tsx
 * 설명:
 *   - WebSocket 에코 연결 상태와 메시지 교환을 보여주는 테스트용 게임 화면이다.
 *   - v0.1.0에서는 간단한 입력/출력 패널만 제공한다.
 * 버전: v0.1.0
 * 관련 설계문서:
 *   - design/frontend/v0.1.0-core-layout-and-routing.md
 *   - design/realtime/v0.1.0-basic-websocket-wiring.md
 * 변경 이력:
 *   - v0.1.0: 에코 송수신 UI 추가
 */
export function GamePage() {
  const { connected, messages, sendMessage } = useEchoSocket()
  const [text, setText] = useState('ping')

  const handleSubmit = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    sendMessage(text)
  }

  return (
    <main className="page">
      <section className="panel">
        <h2>실시간 테스트</h2>
        <p>연결 상태: {connected ? '연결됨' : '연결 대기'}</p>
        <form className="form" onSubmit={handleSubmit}>
          <label>
            메시지
            <input value={text} onChange={(e) => setText(e.target.value)} />
          </label>
          <button className="button" type="submit" disabled={!connected}>
            보내기
          </button>
        </form>
      </section>

      <section className="panel">
        <h2>수신 로그</h2>
        {messages.length === 0 && <p>아직 수신된 메시지가 없습니다.</p>}
        <ul className="list">
          {messages.map((message, index) => (
            <li key={`${message}-${index}`} className="list-item">
              {message}
            </li>
          ))}
        </ul>
      </section>
    </main>
  )
}
