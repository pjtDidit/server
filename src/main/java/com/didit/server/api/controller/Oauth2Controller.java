package com.didit.server.api.controller;

import com.didit.server.api.security.CustomOAuth2User;
import com.didit.server.share.result.Result;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

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
    public Result<Map<String, Object>> hasAuth(@AuthenticationPrincipal CustomOAuth2User user) {
        if (user == null) {
            Result.fail(401, "인증되지 않은 사용자입니다.").throwIfFailure();
        }
        return Result.ok(user.getAttributes());
    }

}
