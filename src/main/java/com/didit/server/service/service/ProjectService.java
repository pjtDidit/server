package com.didit.server.service.service;

import com.didit.server.data.entity.ProjectEntity;
import com.didit.server.service.command.AddProjectCommand;
import com.didit.server.share.result.Result;

import java.util.List;

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
     * Action: ProjectUser에 User를 등록함
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
     * Action: Project를 추가이후 ProjectUser에 자신을 Admin으로 등록
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
}
