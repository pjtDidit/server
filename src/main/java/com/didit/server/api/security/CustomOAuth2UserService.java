package com.didit.server.api.security;

import com.didit.server.data.entity.UserEntity;
import com.didit.server.data.repository.UserRepository;
import com.didit.server.service.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserService userService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 1. 깃허브에서 정보 가져오기
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        log.info("깃허브 데이터: {}", attributes);

        // 2. 필요한 정보 추출하기
        Long githubId = ((Number) attributes.get("id")).longValue();
        String login = attributes.get("login").toString();
        String name = attributes.get("name").toString();
        String avatarUrl = attributes.get("avatar_url").toString();

        UserEntity newUser = userService.joinOrUpdate(githubId, login, name, avatarUrl).getOrThrow();

        return new CustomOAuth2User(oAuth2User, newUser.getId());
    }
}
