package com.didit.server.api.controller;

import com.didit.server.api.request.CreateMeetingRequest;
import com.didit.server.api.security.CustomOAuth2User;
import com.didit.server.service.service.MeetingService;
import com.didit.server.share.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
public class MeetingController {

    private final MeetingService meetingService;

    @PostMapping("/{id}/sessions")
    public ResponseEntity<?> createMeeting(@AuthenticationPrincipal CustomOAuth2User user,
                                           @PathVariable long id,
                                           @RequestBody CreateMeetingRequest request){
        Result<Long> result = meetingService.createMeeting(
                id,
                user.getId(),
                request.title(),
                request.mode()
        );
        return ResponseEntity.ok(result);
    }
}
