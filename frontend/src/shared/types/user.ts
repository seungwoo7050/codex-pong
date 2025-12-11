/**
 * [타입] frontend/src/shared/types/user.ts
 * 설명:
 *   - 인증/프로필 API에서 공통으로 사용하는 사용자 프로필 타입 정의.
 *   - v0.2.0 기본 프로필(닉네임, 아바타, 생성/수정 시각)을 표현한다.
 * 버전: v0.2.0
 * 관련 설계문서:
 *   - design/frontend/v0.2.0-auth-and-profile-ui.md
 */
export interface UserProfile {
  id: number
  username: string
  nickname: string
  avatarUrl?: string | null
  createdAt: string
  updatedAt: string
}
