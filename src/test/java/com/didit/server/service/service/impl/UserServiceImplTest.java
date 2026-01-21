package com.didit.server.service.service.impl;

import com.didit.server.data.entity.UserEntity;
import com.didit.server.data.repository.UserRepository;
import com.didit.server.share.result.Result;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("신규 유저인 경우 데이터를 생성하고 저장에 성공한다.")
    void joinOrUpdate_Success_NewUser() {
        // Given
        Long githubId = 12345L;
        String login = "newbie";
        String name = "New User";
        String avatarUrl = "http://avatar.com/newbie";

        given(userRepository.findByGithubId(githubId)).willReturn(Optional.empty());
        given(userRepository.save(any(UserEntity.class))).willAnswer(invocation -> invocation.getArgument(0));

        // When
        Result<UserEntity> result = userService.joinOrUpdate(githubId, login, name, avatarUrl);

        // Then
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getValue().isPresent()).isTrue();
        assertThat(result.getValue().get().getGithubLogin()).isEqualTo(login);
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    @DisplayName("기존 유저인 경우 정보를 업데이트하고 저장에 성공한다.")
    void joinOrUpdate_Success_ExistingUser() {
        // Given
        Long githubId = 12345L;
        String login = "oldbie";
        String newName = "Updated Name";
        String newAvatar = "http://avatar.com/new";

        UserEntity existingUser = UserEntity.builder()
                .githubId(githubId)
                .githubLogin(login)
                .name("Old Name")
                .avatarUrl("http://avatar.com/old")
                .build();

        given(userRepository.findByGithubId(githubId)).willReturn(Optional.of(existingUser));
        given(userRepository.save(any(UserEntity.class))).willReturn(existingUser);

        // When
        Result<UserEntity> result = userService.joinOrUpdate(githubId, login, newName, newAvatar);

        // Then
        assertThat(result.isSuccess()).isTrue();
        assertThat(existingUser.getName()).isEqualTo(newName);
        assertThat(existingUser.getAvatarUrl()).isEqualTo(newAvatar);
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    @DisplayName("데이터베이스 저장 중 예외가 발생하면 500 에러 Result를 반환한다.")
    void joinOrUpdate_Fail_DatabaseError() {
        // Given
        Long githubId = 1L;
        given(userRepository.findByGithubId(any())).willThrow(new RuntimeException("DB Conn Fail"));

        // When
        Result<UserEntity> result = userService.joinOrUpdate(githubId, "login", "name", "url");

        // Then
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getSingleErrorOrThrow().getCode()).isEqualTo(500);
        assertThat(result.getSingleErrorOrThrow().getMessage()).contains("Internal Server Error");
    }

    @Test
    @DisplayName("기존 사용자의 GitHub 로그인 아이디(username)가 변경된 경우, 로그인 시 새 아이디로 업데이트한다.")
    void joinOrUpdate_UpdateGithubLogin_WhenExistingUserChangesUsername() {
        // Given
        Long githubId = 12345L;
        String oldLogin = "old_id";
        String newLogin = "new_id"; // 변경된 아이디

        UserEntity existingUser = UserEntity.builder()
                .githubId(githubId)
                .githubLogin(oldLogin)
                .name("User")
                .avatarUrl("url")
                .build();

        given(userRepository.findByGithubId(githubId)).willReturn(Optional.of(existingUser));
        given(userRepository.save(any(UserEntity.class))).willReturn(existingUser);

        // When
        Result<UserEntity> result = userService.joinOrUpdate(githubId, newLogin, "User", "url");

        // Then
        assertThat(result.isSuccess()).isTrue();
        // 기존 유저의 githubLogin이 newLogin으로 변경되었는지 검증
        assertThat(existingUser.getGithubLogin()).isEqualTo(newLogin);
        verify(userRepository).save(existingUser);
    }
}