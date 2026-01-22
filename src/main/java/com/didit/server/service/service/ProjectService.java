package com.didit.server.service.service;

import com.didit.server.data.entity.ProjectEntity;
import com.didit.server.data.entity.UserEntity;
import com.didit.server.service.command.AddProjectCommand;
import com.didit.server.share.result.Result;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ProjectService {
    /**
     * Action: 유저 id를 통해 자신이 참가한 project들을 찾음
     * Input:
     *  - long userId
     * Success:
     *  - ok(List<ProjectEntity>) : (반환값 의미 / 값이 없으면 ok())
     * Auth/Permission:
     *  - (로그인 필요 / 본인의 것만)
     * Persistence:
     *  - (읽기)
     * Failures:  // ★ 블랙박스 테스트의 핵심
     *  - <404>: 찾을수 없는 userId
     */
    public Result<List<ProjectEntity>> findProjectsByUserId(long userId);


    //[Feature][Rooms] 방 생성 + 생성 즉시 목록 반영 (POST /api/v1/rooms) #5
    /**
     * Action: ProjectUser에 User Admin으로 등록함
     * Input:
     *  - long userId
     *  - long projectId
     * Success:
     *  - ok()
     * Auth/Permission:
     *  - (로그인 필요 / 본인만)
     * Persistence:
     *  - (추가)
     * Failures:  // ★ 블랙박스 테스트의 핵심
     *  - <404>: 찾을수 없는 userId
     *  - <404>: 찾을수 없는 projectId
     */
    Result AddProjectAdminUser(long userId, long projectId);

    /**
     * Action: Project를 추가이후 AddProjectAdminUser를 호출해 ProjectUser에 자신을 Admin으로 등록
     * Input:
     *  - long ownerUserId
     *  - String projectName
     *  - String repoFullName
     * Success:
     *  - ok()
     * Auth/Permission:
     *  - (로그인 필요 / 본인만)
     * Persistence:
     *  - (추가)
     * Failures:  // ★ 블랙박스 테스트의 핵심
     *  - <404>: 찾을수 없는 userId
     *  - <407>: 중복되는 repoFullName
     */
    Result AddProject(AddProjectCommand cmd);



    /**
     * Action: Project에대한 초대코드 생성. 어드민 유저만 생성가능, save이후 생선된 UUID 리턴
     * Input:
     *  - long userId
     *  - long projectId
     *  - LocalDateTime expiresAt
     * Success:
     *  - ok(UUID)
     * Auth/Permission:
     *  - (어드민만)
     * Persistence:
     *  - (추가)
     * Failures:  // ★ 블랙박스 테스트의 핵심
     *  - <404> NotFoundError: 찾을수 없는 userId - resourceName = userId
     *  - <404> NotFoundError: 찾을수 없는 projectId - resourceName = projectId
     *  - <403> ForbiddenError: 중복되는 repoFullName
     */
    Result<UUID> AddInviteCode(long userId, long projectId, LocalDateTime expiresAt);

    /**
     * Action: 초대 코드(UUID)를 통해 프로젝트 정보 얻기
     * Input:
     *  - UUID inviteCode
     * Success:
     *  - ok(ProjectEntity)
     * Auth/Permission:
     *  - (누구나)
     * Persistence:
     *  - (추가)
     * Failures:  // ★ 블랙박스 테스트의 핵심
     *  - <404> NotFoundError: 찾을수 없는 inviteCode - resourceName = inviteCode
     *  - <410> GoneError: expire된 inviteCode
     */
    Result<ProjectEntity> FindProjectByInviteCode(UUID inviteCode);


    /**
     * Action: ProjectUser에 User Member으로 등록함
     * Input:
     *  - long userId
     *  - long projectId
     * Success:
     *  - ok()
     * Auth/Permission:
     *  - (로그인 필요 / 본인만)
     * Persistence:
     *  - (추가)
     * Failures:  // ★ 블랙박스 테스트의 핵심
     *  - <404>: 찾을수 없는 userId
     *  - <404>: 찾을수 없는 projectId
     */
    Result AddProjectUser(long userId, long projectId);

    /**
     * Action: Project에 속해있는 유저들을 반환함
     * Input:
     *  - long projectId
     * Success:
     *  - ok(List<UserEntity>)
     * Auth/Permission:
     *  - (누구나)
     * Persistence:
     *  - (찾기)
     * Failures:  // ★ 블랙박스 테스트의 핵심
     *  - <404>: 찾을수 없는 projectId
     */
    Result<List<UserEntity>> FindUsersInProject(long projectId);
}
