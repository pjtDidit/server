package com.didit.server.service.service.impl;

import com.didit.server.data.entity.MeetingEntity;
import com.didit.server.data.entity.ProjectEntity;
import com.didit.server.data.entity.UserEntity;
import com.didit.server.data.entity.enums.MeetingMode;
import com.didit.server.data.entity.enums.MeetingStatus;
import com.didit.server.data.repository.MeetingRepository;
import com.didit.server.data.repository.ProjectRepository;
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
class MeetingServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MeetingRepository meetingRepository;

    @InjectMocks
    private MeetingServiceImpl meetingService;

    @Test
    @DisplayName("유효한 입력값이 주어지면 회의실 생성에 성공하고 ID를 반환한다.")
    void createMeeting_Success() {
        // Given
        long projectId = 1L;
        long userId = 1L;
        String title = "데브코스 1회차 회의";
        MeetingMode mode = MeetingMode.VOICE;

        ProjectEntity project = ProjectEntity.builder().id(projectId).build();
        UserEntity user = UserEntity.builder().id(userId).build();
        MeetingEntity savedMeeting = MeetingEntity.builder().id(100L).build();

        given(projectRepository.findById(projectId)).willReturn(Optional.of(project));
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(meetingRepository.save(any(MeetingEntity.class))).willReturn(savedMeeting);

        // When
        Result<Long> result = meetingService.createMeeting(projectId, userId, title, mode);

        // Then
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getValue().get()).isEqualTo(100L);
        verify(meetingRepository, times(1)).save(any(MeetingEntity.class));
    }

    @Test
    @DisplayName("존재하지 않는 projectId가 주어지면 NotFoundError를 반환한다.")
    void createMeeting_Fail_InvalidProjectId() {
        // Given
        long invalidProjectId = 999L;
        given(projectRepository.findById(invalidProjectId)).willReturn(Optional.empty());

        // When
        Result<Long> result = meetingService.createMeeting(invalidProjectId, 1L, "제목", MeetingMode.CHAT);

        // Then
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getSingleErrorOrThrow().getStatus()).isEqualTo(404);
        assertThat(result.getSingleErrorOrThrow().getMessage()).contains("projectId");
    }

    @Test
    @DisplayName("제목이 비어있으면 400 에러를 반환한다.")
    void createMeeting_Fail_EmptyTitle() {
        // Given
        long projectId = 1L;
        long userId = 1L;
        String emptyTitle = "";

        given(projectRepository.findById(projectId)).willReturn(Optional.of(ProjectEntity.builder().id(projectId).build()));
        given(userRepository.findById(userId)).willReturn(Optional.of(UserEntity.builder().id(userId).build()));

        // When
        Result<Long> result = meetingService.createMeeting(projectId, userId, emptyTitle, MeetingMode.VOICE);

        // Then
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getSingleErrorOrThrow().getStatus()).isEqualTo(400);
    }

    @Test
    @DisplayName("데이터베이스 저장 중 예외가 발생하면 ServerError(500)를 반환한다.")
    void createMeeting_Fail_InternalServerError() {
        // Given
        long projectId = 1L;
        given(projectRepository.findById(projectId)).willReturn(Optional.of(ProjectEntity.builder().id(projectId).build()));
        given(userRepository.findById(anyLong())).willReturn(Optional.of(UserEntity.builder().id(1L).build()));
        given(meetingRepository.save(any())).willThrow(new RuntimeException("DB Error"));

        // When
        Result<Long> result = meetingService.createMeeting(projectId, 1L, "제목", MeetingMode.VOICE);

        // Then
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getSingleErrorOrThrow().getStatus()).isEqualTo(500);
        assertThat(result.getSingleErrorOrThrow().getMessage()).contains("something wrong");
    }
}