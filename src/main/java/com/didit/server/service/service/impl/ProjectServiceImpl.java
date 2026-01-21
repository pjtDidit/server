package com.didit.server.service.service.impl;

import com.didit.server.data.entity.ProjectEntity;
import com.didit.server.data.entity.ProjectUserEntity;
import com.didit.server.data.repository.ProjectUserRepository;
import com.didit.server.data.repository.UserRepository;
import com.didit.server.service.service.ProjectService;
import com.didit.server.share.result.Result;
import com.didit.server.share.result.impl.NotFoundError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectServiceImpl implements ProjectService {
    private final ProjectUserRepository _ProjectUserRepository;
    private final UserRepository _UserRepository;

    public ProjectServiceImpl(ProjectUserRepository _ProjectUserRepository, UserRepository userRepository) {
        this._ProjectUserRepository = _ProjectUserRepository;
        _UserRepository = userRepository;
    }

    @Override
    public Result<List<ProjectEntity>> findProjectsByUserId(long userId) {
        var user = _UserRepository.findById(userId);
        if(user.isEmpty()){
            return Result.fail(new NotFoundError("userId", userId));
        }
        var projectUsers = _ProjectUserRepository.findAllByUser_Id(userId);
        var projects = projectUsers.stream().map(ProjectUserEntity::getProject).toList();
        return Result.ok(projects);
    }
}
