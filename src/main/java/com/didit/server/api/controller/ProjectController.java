package com.didit.server.api.controller;

import com.didit.server.api.request.AddProjectInviteRequest;
import com.didit.server.api.request.AddProjectRequest;
import com.didit.server.api.response.FindProjectResponse;
import com.didit.server.api.response.UserResponse;
import com.didit.server.api.security.CustomOAuth2User;
import com.didit.server.service.command.AddProjectCommand;
import com.didit.server.service.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService _ProjectService;

    //[Feature][Rooms] 방 목록 조회 + 사이드바 연동 (GET /api/v1/rooms)
    @GetMapping()
    public ResponseEntity<?> findProjects(@AuthenticationPrincipal CustomOAuth2User user){
        var userId = user.getId();

        var findResult = _ProjectService.findProjectsByUserId(userId);
        if(findResult.isFailure()){
            var err = findResult.getSingleErrorOrThrow().getResponse();
            return new ResponseEntity<>(err, err.getStatusCode());
        }

        var entities = findResult.getOrThrow();
        var responses = entities.stream().map(FindProjectResponse::fromEntity)
                .toList();

        return ResponseEntity.ok(responses);
    }

    //[Feature][Rooms] 방 생성 + 생성 즉시 목록 반영 (POST /api/v1/rooms) #5
    @PostMapping()
    public ResponseEntity<?> AddProject(@AuthenticationPrincipal CustomOAuth2User user,
                                        @RequestBody @Valid AddProjectRequest request){
        var userId = user.getId();

        var addResult = _ProjectService.AddProject(new AddProjectCommand(userId, request.projectName(), request.githubUrl()));
        if(addResult.isFailure()){
            var err = addResult.getSingleErrorOrThrow().getResponse();
            return new ResponseEntity<>(err, err.getStatusCode());
        }

        return ResponseEntity.ok().build();
    }

    //[Feature][Invites] 초대 코드/링크 생성 (POST /api/v1/projects/{id}/invites)
    @PostMapping("/invites")
    public ResponseEntity<?> AddInvite(@AuthenticationPrincipal CustomOAuth2User user,
                                       @RequestBody @Valid AddProjectInviteRequest request){
        var userId = user.getId();
        var expire = request.expireDate() == null ? LocalDateTime.now().plusYears(1000) : request.expireDate();
        var addResult = _ProjectService.AddInviteCode(userId, request.projectId(), expire);
        if(addResult.isFailure()){
            var err = addResult.getSingleErrorOrThrow().getResponse();
            return new ResponseEntity<>(err, err.getStatusCode());
        }

        return ResponseEntity.ok(addResult.getValue());
    }

    //[Feature][Join] 초대 코드로 방 참여 (POST /api/v1/rooms/projects/{inviteCode})
    @GetMapping("/invites/{inviteCode}")
    public ResponseEntity<?> GetProjectByInviteCode(@PathVariable String inviteCode){
        UUID uuid;
        try{
            uuid = UUID.fromString(inviteCode);
        }
        catch (IllegalArgumentException exception){
            return ResponseEntity.badRequest().build();
        }
        var findResult = _ProjectService.FindProjectByInviteCode(uuid);
        if(findResult.isFailure()){
            var err = findResult.getSingleErrorOrThrow().getResponse();
            return new ResponseEntity<>(err, err.getStatusCode());
        }
        var entity = findResult.getValue().get();
        return ResponseEntity.ok(FindProjectResponse.fromEntity(entity));
    }

    @PostMapping("invites/{inviteCode}")
    public ResponseEntity<?> AddProjectUserByInviteCode(@AuthenticationPrincipal CustomOAuth2User user,
                                                        @PathVariable String inviteCode){
        UUID uuid;
        try{
            uuid = UUID.fromString(inviteCode);
        }
        catch (IllegalArgumentException exception){
            return ResponseEntity.badRequest().build();
        }

        var findResult = _ProjectService.FindProjectByInviteCode(uuid);
        if(findResult.isFailure()){
            var err = findResult.getSingleErrorOrThrow().getResponse();
            return new ResponseEntity<>(err, err.getStatusCode());
        }

        var userId = user.getId();
        var entity = findResult.getValue().get();

        var addResult = _ProjectService.AddProjectUser(userId, entity.getId());
        if(addResult.isFailure()){
            var err = addResult.getSingleErrorOrThrow().getResponse();
            return new ResponseEntity<>(err, err.getStatusCode());
        }

        return ResponseEntity.ok().build();
    }

    //[Feature][Participants] 방 참여자 목록 조회 (GET /api/v1/projects/{id}/participants) #8
    @GetMapping("/{projectId}/participants")
    public ResponseEntity<?> findUsersInProject(@PathVariable Long projectId){
        var findResult = _ProjectService.FindUsersInProject(projectId);
        if(findResult.isFailure()){
            var err = findResult.getSingleErrorOrThrow().getResponse();
            return new ResponseEntity<>(err, err.getStatusCode());
        }

        var entities = findResult.getOrThrow();
        var response = entities.stream().map(x -> UserResponse.builder()
                .id(x.getId())
                .githubId(x.getGithubId())
                .githubLogin(x.getGithubLogin())
                .name(x.getName())
                .avatarUrl(x.getAvatarUrl())
                .createdAt(x.getCreatedAt())
                .lastLoginAt(x.getLastLoginAt())
                .build()).toList();

        return ResponseEntity.ok(response);
    }

}
