package com.didit.server.service.service.impl;

import com.didit.server.data.entity.UserEntity;
import com.didit.server.data.repository.UserRepository;
import com.didit.server.service.service.UserService;
import com.didit.server.share.result.Result;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Transactional
    public Result<UserEntity> joinOrUpdate(Long githubId, String login, String name, String avatarUrl) {
        try {
            UserEntity user = userRepository.findByGithubId(githubId).orElse(null);

            if (user == null) {
                user = UserEntity.builder()
                        .githubId(githubId)
                        .githubLogin(login)
                        .name(name)
                        .avatarUrl(avatarUrl)
                        .createdAt(LocalDateTime.now())
                        .lastLoginAt(LocalDateTime.now())
                        .build();
            } else {
                user.setAvatarUrl(avatarUrl);
                user.setName(name);
                user.setLastLoginAt(LocalDateTime.now());
            }

            return Result.ok(userRepository.save(user));
        } catch (Exception e) {
            return Result.fail(500, "Internal Server Error");
        }
    }
}

