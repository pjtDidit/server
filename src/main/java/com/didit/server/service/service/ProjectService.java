package com.didit.server.service.service;

import com.didit.server.data.entity.ProjectEntity;
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
}
