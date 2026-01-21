package com.didit.server.api.controller;

import com.didit.server.api.request.AddProjectRequest;
import com.didit.server.api.response.ErrorResponse;
import com.didit.server.api.response.FindProjectsResponse;
import com.didit.server.api.security.CustomOAuth2User;
import com.didit.server.service.command.AddProjectCommand;
import com.didit.server.service.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
        var responses = entities.stream().map(x -> FindProjectsResponse.builder()
                .id(x.getId())
                .name(x.getName())
                .ownerId(x.getOwner().getId())
                .repoId(x.getRepoId())
                .repoFullName(x.getRepoFullName())
                .thumbnailUrl(x.getThumbnailUrl())
                .createdAt(x.getCreatedAt())
                .updatedAt(x.getUpdatedAt())
                .build())
                .toList();

        return ResponseEntity.ok(responses);
    }

    //[Feature][Rooms] 방 생성 + 생성 즉시 목록 반영 (POST /api/v1/rooms) #5
    @PostMapping()
    public ResponseEntity<?> AddProject(@AuthenticationPrincipal CustomOAuth2User user,
                                        @RequestBody AddProjectRequest request){
        var userId = user.getId();

        var addResult = _ProjectService.AddProject(new AddProjectCommand(userId, request.projectName(), request.githubUrl()));
        if(addResult.isFailure()){
            var err = addResult.getSingleErrorOrThrow().getResponse();
            return new ResponseEntity<>(err, err.getStatusCode());
        }

        return ResponseEntity.noContent().build();
    }
}
