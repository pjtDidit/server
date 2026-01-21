package com.didit.server.api.controller;

import com.didit.server.api.security.CustomOAuth2User;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class Oauth2Controller {

    @GetMapping("/v1/auth/login")
    public void login(HttpServletResponse response) throws IOException {
        response.sendRedirect("/oauth2/authorization/github");
    }

    // 세션 유지 확인용 테스트 엔드포인트
    @GetMapping("/v1/user/me")
    public ResponseEntity<?> me(@AuthenticationPrincipal CustomOAuth2User user) {
        if (user == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(user.getAttributes());
    }

}
