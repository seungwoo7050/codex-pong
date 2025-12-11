/**
 * [타입] frontend/src/shared/types/game.ts
 * 설명:
 *   - v0.3.0 실시간 게임 상태를 표현하는 공통 타입 정의다.
 * 관련 설계문서:
 *   - design/frontend/v0.3.0-game-lobby-and-play-ui.md
 *   - design/realtime/v0.3.0-game-loop-and-events.md
 */
export interface GameSnapshot {
  roomId: string
  ballX: number
  ballY: number
  ballVelocityX: number
  ballVelocityY: number
  leftPaddleY: number
  rightPaddleY: number
  leftScore: number
  rightScore: number
  targetScore: number
  finished: boolean
}

export interface GameServerMessage {
  type: 'READY' | 'STATE' | 'FINISHED'
  snapshot: GameSnapshot
}
