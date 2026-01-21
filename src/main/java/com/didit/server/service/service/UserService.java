package com.didit.server.service.service;

import com.didit.server.data.entity.UserEntity;
import com.didit.server.share.result.Result;

public interface UserService {

    /**
     * Action: 깃허브 OAuth 인증 정보를 바탕으로 신규 사용자를 가입시키거나 기존 사용자 정보를 업데이트한다.
     * Input:
     * - githubId: 깃허브에서 제공하는 고유 숫자 ID (필수, Long)
     * - login: 사용자의 깃허브 아이디 (필수, String)
     * - name: 사용자의 이름 (null 가능, null일 경우 login 값을 이름으로 대체하여 저장)
     * - avatarUrl: 프로필 이미지 경로 (필수, String)
     * Success:
     * - ok(UserEntity): 처리가 완료된 사용자 엔티티 반환 (신규/수정 포함)
     * Auth/Permission:
     * - 로그인 필요 없음 (로그인 과정 자체에서 호출되는 메서드이므로 Guest 권한 허용)
     * Persistence:
     * - 쓰기 작업 수행. 조회 후 데이터가 없으면 insert, 있으면 update 후 save 호출.
     * Idempotency:
     * - 멱등(Idempotent): 같은 정보로 여러 번 요청해도 결과적으로 동일한 유저 정보가 유지됨.
     * Failures:
     * - 500: 데이터베이스 저장 중 예상치 못한 런타임 예외 발생 (metadata: {resource: "UserEntity"})
     * - 400: 입력값(githubId, login 등)이 유효하지 않거나 누락된 경우
     */
    Result<UserEntity> joinOrUpdate(Long githubId, String login, String name, String avatarUrl);
}
