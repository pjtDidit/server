package com.didit.server.api.security;

import lombok.Getter;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Getter
public class CustomOAuth2User extends DefaultOAuth2User {
    private final Long id;

    public CustomOAuth2User(OAuth2User oAuth2User,
                            Long id) {
        super(oAuth2User.getAuthorities(), oAuth2User.getAttributes(), "id");
        this.id = id;
    }
}
