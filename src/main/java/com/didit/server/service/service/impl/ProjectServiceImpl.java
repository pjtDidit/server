package com.didit.server.service.service.impl;

import com.didit.server.data.entity.ProjectEntity;
import com.didit.server.data.entity.ProjectInviteEntity;
import com.didit.server.data.entity.ProjectUserEntity;
import com.didit.server.data.entity.UserEntity;
import com.didit.server.data.entity.enums.ProjectUserRole;
import com.didit.server.data.entity.enums.ProjectUserStatus;
import com.didit.server.data.repository.ProjectInviteRepository;
import com.didit.server.data.repository.ProjectRepository;
import com.didit.server.data.repository.ProjectUserRepository;
import com.didit.server.data.repository.UserRepository;
import com.didit.server.service.command.AddProjectCommand;
import com.didit.server.service.service.ProjectService;
import com.didit.server.share.result.Result;
import com.didit.server.share.result.impl.ConflictError;
import com.didit.server.share.result.impl.ForbiddenError;
import com.didit.server.share.result.impl.GoneError;
import com.didit.server.share.result.impl.NotFoundError;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final ProjectUserRepository _ProjectUserRepository;
    private final UserRepository _UserRepository;
    private final ProjectRepository _ProjectRepository;
    private final ProjectInviteRepository _ProjectInviteRepository;

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

    @Override
    public Result<UUID> AddInviteCode(long userId, long projectId, LocalDateTime expiresAt) {
        var user = _UserRepository.findById(userId);
        if(user.isEmpty()){
            return Result.fail(new NotFoundError("userId", userId));
        }
        var project = _ProjectRepository.findById(projectId);
        if(project.isEmpty()){
            return Result.fail(new NotFoundError("projectId", projectId));
        }
        var projectUser =_ProjectUserRepository.findByProject_IdAndUser_Id(projectId, userId);
        if(projectUser.isEmpty() || !projectUser.get().getRole().equals(ProjectUserRole.ADMIN)){
            return Result.fail(new ForbiddenError("허용 되지 않는 유저입니다."));
        }

        var inviteCode = UUID.randomUUID();
        var entity = ProjectInviteEntity.builder()
                .id(inviteCode.toString())
                .project(project.get())
                .user(user.get())
                .expiresAt(expiresAt)
                .build();

        _ProjectInviteRepository.save(entity);

        return Result.ok(inviteCode);
    }

    @Override
    public Result<ProjectEntity> FindProjectByInviteCode(UUID inviteCode) {
        var invite = _ProjectInviteRepository.findById(inviteCode.toString());
        if(invite.isEmpty()){
            return Result.fail(new NotFoundError("inviteCode", inviteCode));
        }
        if(invite.get().getExpiresAt().isBefore(LocalDateTime.now())){
            return Result.fail(new GoneError("expired inviteCode"));
        }

        return Result.ok(invite.get().getProject());
    }

    @Override
    public Result AddProjectUser(long userId, long projectId) {
        var user = _UserRepository.findById(userId);
        if(user.isEmpty()){
            return Result.fail(new NotFoundError("userId", userId));
        }
        var project = _ProjectRepository.findById(projectId);
        if(project.isEmpty()){
            return Result.fail(new NotFoundError("projectId", projectId));
        }

        var projectUser = _ProjectUserRepository.findByProject_IdAndUser_Id(projectId, userId);
        if(projectUser.isPresent()){
            return Result.fail(new ConflictError("이미 존재하는 유저입니다."));
        }

        var projectUserEntity = ProjectUserEntity.builder()
                .project(project.get())
                .user(user.get())
                .role(ProjectUserRole.MEMBER)
                .status(ProjectUserStatus.ACTIVE)
                .joinedAt(LocalDateTime.now())
                .build();

        _ProjectUserRepository.save(projectUserEntity);

        return Result.ok();
    }

    @Override
    public Result<List<UserEntity>> FindUsersInProject(long projectId) {
        var project = _ProjectRepository.findById(projectId);
        if(project.isEmpty()){
            return Result.fail(new NotFoundError("projectId", projectId));
        }

        var list = _ProjectUserRepository.findAllByProject_Id(projectId);

        var users = list.stream().map(ProjectUserEntity::getUser).toList();

        return Result.ok(users);
    }
}
