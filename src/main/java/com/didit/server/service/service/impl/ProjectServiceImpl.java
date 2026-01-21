package com.didit.server.service.service.impl;

import com.didit.server.data.entity.ProjectEntity;
import com.didit.server.data.entity.ProjectUserEntity;
import com.didit.server.data.entity.enums.ProjectUserRole;
import com.didit.server.data.entity.enums.ProjectUserStatus;
import com.didit.server.data.repository.ProjectRepository;
import com.didit.server.data.repository.ProjectUserRepository;
import com.didit.server.data.repository.UserRepository;
import com.didit.server.service.command.AddProjectCommand;
import com.didit.server.service.service.ProjectService;
import com.didit.server.share.result.Result;
import com.didit.server.share.result.impl.ConflictError;
import com.didit.server.share.result.impl.NotFoundError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProjectServiceImpl implements ProjectService {
    private final ProjectUserRepository _ProjectUserRepository;
    private final UserRepository _UserRepository;
    private final ProjectRepository _ProjectRepository;

    public ProjectServiceImpl(ProjectUserRepository _ProjectUserRepository, UserRepository userRepository, ProjectRepository projectRepository) {
        this._ProjectUserRepository = _ProjectUserRepository;
        _UserRepository = userRepository;
        _ProjectRepository = projectRepository;
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

    @Override
    public Result AddProjectAdminUser(long userId, long projectId) {
        var user = _UserRepository.findById(userId);
        if(user.isEmpty()){
            return Result.fail(new NotFoundError("userId", userId));
        }
        var project = _ProjectRepository.findById(projectId);
        if(project.isEmpty()){
            return Result.fail(new NotFoundError("projectId", projectId));
        }

        var projectUser = ProjectUserEntity.builder()
                .project(project.get())
                .user(user.get())
                .role(ProjectUserRole.ADMIN)
                .status(ProjectUserStatus.ACTIVE)
                .joinedAt(LocalDateTime.now())
                .build();

        _ProjectUserRepository.save(projectUser);

        return Result.ok();
    }

    @Override
    public Result AddProject(AddProjectCommand cmd) {
        var user = _UserRepository.findById(cmd.ownerUserId());
        if(user.isEmpty()){
            return Result.fail(new NotFoundError("userId", cmd.ownerUserId()));
        }
        if(_ProjectRepository.existsByRepoFullName(cmd.repoFullName())){
            return Result.fail(new ConflictError("이미 등록된 깃허브 repo 입니다."));
        }

        var project = ProjectEntity
                .builder()
                .name(cmd.projectName())
                .owner(user.get())
                .repoFullName(cmd.repoFullName())
                .build();

        _ProjectRepository.save(project);

        var addResult = AddProjectAdminUser(cmd.ownerUserId(), project.getId());
        if(addResult.isFailure()) return Result.fail(addResult.getErrors());

        return Result.ok();
    }
}
