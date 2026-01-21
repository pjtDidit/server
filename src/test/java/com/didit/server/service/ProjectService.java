package com.didit.server.service; // ✅ 운영 코드 패키지 구조 미러링 권장 :contentReference[oaicite:1]{index=1}

import com.didit.server.data.entity.ProjectEntity;
import com.didit.server.data.entity.ProjectUserEntity;
import com.didit.server.data.entity.UserEntity;
import com.didit.server.data.entity.enums.ProjectUserRole;
import com.didit.server.data.entity.enums.ProjectUserStatus;
import com.didit.server.data.repository.ProjectRepository;
import com.didit.server.data.repository.ProjectUserRepository;
import com.didit.server.data.repository.UserRepository;

// TODO: 네 프로젝트 Result / ResultError 실제 패키지로 수정
import com.didit.server.service.command.AddProjectCommand;
import com.didit.server.service.service.impl.ProjectServiceImpl;
import com.didit.server.share.result.Result;
import com.didit.server.share.result.ResultError;

import com.navercorp.fixturemonkey.FixtureMonkey;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProjectQueryServiceImpl 유닛 테스트") // 권장 :contentReference[oaicite:2]{index=2}
class ProjectServiceImplTest {

    private final FixtureMonkey fixtureMonkey =
            FixtureMonkey.builder().defaultNotNull(true).build(); // 기본 설정 OK :contentReference[oaicite:3]{index=3}

    @Mock private UserRepository userRepository;
    @Mock private ProjectUserRepository projectUserRepository;
    @Mock private ProjectRepository projectRepository;

    @InjectMocks private ProjectServiceImpl sut;

    @Test
    @DisplayName("유저가 존재하고 참가 프로젝트가 있으면 ok(List<ProjectEntity>)를 반환한다")
    void findProjectsByUserId_userExistsAndHasProjects_returnsOk() {
        // Given (fixture + mock stub) :contentReference[oaicite:4]{index=4}
        long userId = 10L; // 분기/조회 키는 랜덤 금지 :contentReference[oaicite:5]{index=5}
        UserEntity user = aUser(userId);

        ProjectEntity p1 = aProject(101L, user);
        ProjectEntity p2 = aProject(102L, user);

        ProjectUserEntity pu1 = aProjectUser(user, p1);
        ProjectUserEntity pu2 = aProjectUser(user, p2);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(projectUserRepository.findAllByUser_Id(userId)).thenReturn(List.of(pu1, pu2));

        // When (SUT 호출) :contentReference[oaicite:6]{index=6}
        Result<List<ProjectEntity>> result = sut.findProjectsByUserId(userId);

        // Then (Result 계약 + 값 검증 + 상호작용 검증) :contentReference[oaicite:7]{index=7}
        assertTrue(result.isSuccess());
        assertTrue(result.getErrors().isEmpty()); // 성공이면 errors empty :contentReference[oaicite:8]{index=8}

        List<ProjectEntity> projects = result.getValue().orElseThrow();
        assertEquals(2, projects.size());
        assertTrue(projects.stream().anyMatch(p -> idOf(p).equals(101L)));
        assertTrue(projects.stream().anyMatch(p -> idOf(p).equals(102L)));

        verify(userRepository, times(1)).findById(userId);
        verify(projectUserRepository, times(1)).findAllByUser_Id(userId);
    }

    @Test
    @DisplayName("유저가 존재하지만 참가 프로젝트가 없으면 ok(emptyList)를 반환한다")
    void findProjectsByUserId_userExistsButNoProjects_returnsOk() {
        // Given
        long userId = 11L;
        UserEntity user = aUser(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(projectUserRepository.findAllByUser_Id(userId)).thenReturn(List.of());

        // When
        Result<List<ProjectEntity>> result = sut.findProjectsByUserId(userId);

        // Then
        assertTrue(result.isSuccess());
        assertTrue(result.getErrors().isEmpty());
        List<ProjectEntity> projects = result.getValue().orElseThrow();
        assertTrue(projects.isEmpty());

        verify(userRepository, times(1)).findById(userId);
        verify(projectUserRepository, times(1)).findAllByUser_Id(userId);
    }

    @Test
    @DisplayName("존재하지 않는 userId이면 fail(404 NotFound) 을 반환한다")
    void findProjectsByUserId_userNotFound_returnsFailNotFound() {
        // Given
        long userId = 9999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When
        Result<List<ProjectEntity>> result = sut.findProjectsByUserId(userId);

        // Then (실패 계약 + 에러 고정(코드/metadata 등)) :contentReference[oaicite:9]{index=9}
        assertTrue(result.isFailure());
        assertNotNull(result.getErrors());
        assertFalse(result.getErrors().isEmpty());
        assertTrue(result.getValue().isEmpty()); // 실패면 value 없음 :contentReference[oaicite:10]{index=10}

        ResultError err = result.getErrors().getFirst();
        assertEquals(404, err.getCode()); // code/status는 최우선 고정 :contentReference[oaicite:11]{index=11}

        // metadata까지 고정(가능하면) :contentReference[oaicite:12]{index=12}
        Map<String, Object> meta = err.getMetadata();
        assertNotNull(meta);
        assertTrue(meta.containsKey("resource"));
        assertEquals("userId", String.valueOf(meta.get("resource")));

        // (프로젝트 구현에 따라 key가 userId로 들어간다는 가정)
        if (meta.containsKey("key")) {
            assertEquals(String.valueOf(userId), String.valueOf(meta.get("key")));
        }

        // 실패 케이스에서 부수효과 없음 검증(중요) :contentReference[oaicite:13]{index=13}
        verify(projectUserRepository, never()).findAllByUser_Id(anyLong());
    }

    // --------------------------
    // AddProjectAdminUser(userId, projectId)
    // --------------------------

    @Test
    @DisplayName("유저/프로젝트가 존재하면 ProjectUser를 ADMIN으로 추가하고 ok()를 반환한다")
    void AddProjectAdminUser_userAndProjectExist_savesAdminProjectUser_returnsOk() {
        // Given
        long userId = 10L;
        long projectId = 100L;

        var user = aUser(userId);
        var project = aProject(projectId, aUser(1L));

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(projectUserRepository.save(any(ProjectUserEntity.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        // When
        Result result = sut.AddProjectAdminUser(userId, projectId);

        // Then
        assertTrue(result.isSuccess());
        assertTrue(result.getErrors().isEmpty());

        ArgumentCaptor<ProjectUserEntity> captor = ArgumentCaptor.forClass(ProjectUserEntity.class);
        verify(projectUserRepository, times(1)).save(captor.capture());

        ProjectUserEntity saved = captor.getValue();
        assertSame(user, saved.getUser());
        assertSame(project, saved.getProject());
        assertEquals(ProjectUserRole.ADMIN, saved.getRole());
        assertEquals(ProjectUserStatus.ACTIVE, saved.getStatus());
    }

    @Test
    @DisplayName("존재하지 않는 userId이면 fail(404)를 반환하고 저장하지 않는다")
    void AddProjectAdminUser_userNotFound_returnsFail404_andNoSideEffect() {
        // Given
        long userId = 9999L;
        long projectId = 100L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When
        Result result = sut.AddProjectAdminUser(userId, projectId);

        // Then
        assertTrue(result.isFailure());
        assertFalse(result.getErrors().isEmpty());
        assertTrue(result.getValue().isEmpty());

        var err = (ResultError)result.getErrors().getFirst();
        assertEquals(404, err.getCode());

        verify(projectRepository, never()).findById(anyLong());
        verify(projectUserRepository, never()).save(any());
    }

    @Test
    @DisplayName("존재하지 않는 projectId이면 fail(404)를 반환하고 저장하지 않는다")
    void AddProjectAdminUser_projectNotFound_returnsFail404_andNoSideEffect() {
        // Given
        long userId = 10L;
        long projectId = 9999L;

        var user = aUser(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        // When
        Result result = sut.AddProjectAdminUser(userId, projectId);

        // Then
        assertTrue(result.isFailure());
        assertFalse(result.getErrors().isEmpty());
        assertTrue(result.getValue().isEmpty());

        var err = (ResultError)result.getErrors().getFirst();
        assertEquals(404, err.getCode());

        verify(projectUserRepository, never()).save(any());
    }

    // --------------------------
    // AddProject(AddProjectCommand cmd)
    // --------------------------

    @Test
    @DisplayName("owner가 존재하고 repoFullName이 중복이 아니면 Project 생성 후 owner를 ADMIN으로 등록하고 ok()를 반환한다")
    void AddProject_ownerExistsAndRepoNotDuplicated_createsProjectAndRegistersAdmin_returnsOk() {
        // Given
        long ownerUserId = 10L;
        String projectName = "demo";
        String repoFullName = "org/repo";

        var owner = aUser(ownerUserId);
        AddProjectCommand cmd = new AddProjectCommand(ownerUserId, projectName, repoFullName);

        when(userRepository.findById(ownerUserId)).thenReturn(Optional.of(owner));

        // 구현이 existsByRepoFullName을 쓰면 이 라인 사용
        when(projectRepository.existsByRepoFullName(repoFullName)).thenReturn(false);

        // 구현이 findByRepoFullName을 쓰면 위 라인 대신 아래 사용
        // when(projectRepository.findByRepoFullName(repoFullName)).thenReturn(Optional.empty());

        Map<Long, ProjectEntity> store = new HashMap<>();

        when(projectRepository.save(any(ProjectEntity.class)))
                .thenAnswer(inv -> {
                    ProjectEntity p = inv.getArgument(0);
                    ReflectionTestUtils.setField(p, "id", 123L);
                    store.put(123L, p);
                    return p;
                });

        when(projectRepository.findById(anyLong()))
                .thenAnswer(inv -> Optional.ofNullable(store.get(inv.getArgument(0))));
        when(projectUserRepository.save(any(ProjectUserEntity.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        // When
        Result result = sut.AddProject(cmd);

        // Then
        assertTrue(result.isSuccess());
        assertTrue(result.getErrors().isEmpty());

        ArgumentCaptor<ProjectEntity> projectCaptor = ArgumentCaptor.forClass(ProjectEntity.class);
        verify(projectRepository, times(1)).save(projectCaptor.capture());
        ProjectEntity createdProject = projectCaptor.getValue();

        assertEquals(projectName, createdProject.getName());
        assertSame(owner, createdProject.getOwner());
        assertEquals(repoFullName, createdProject.getRepoFullName());

        ArgumentCaptor<ProjectUserEntity> puCaptor = ArgumentCaptor.forClass(ProjectUserEntity.class);
        verify(projectUserRepository, times(1)).save(puCaptor.capture());
        ProjectUserEntity createdPu = puCaptor.getValue();

        assertSame(owner, createdPu.getUser());
        assertSame(createdProject, createdPu.getProject());
        assertEquals(ProjectUserRole.ADMIN, createdPu.getRole());
        assertEquals(ProjectUserStatus.ACTIVE, createdPu.getStatus());
    }

    @Test
    @DisplayName("존재하지 않는 ownerUserId이면 fail(404)를 반환하고 아무것도 저장하지 않는다")
    void AddProject_ownerNotFound_returnsFail404_andNoSideEffect() {
        // Given
        long ownerUserId = 9999L;
        AddProjectCommand cmd = new AddProjectCommand(ownerUserId, "demo", "org/repo");

        when(userRepository.findById(ownerUserId)).thenReturn(Optional.empty());

        // When
        Result result = sut.AddProject(cmd);

        // Then
        assertTrue(result.isFailure());
        assertFalse(result.getErrors().isEmpty());
        assertTrue(result.getValue().isEmpty());

        var err = (ResultError)result.getErrors().get(0);
        assertEquals(404, err.getCode());

        verify(projectRepository, never()).save(any());
        verify(projectUserRepository, never()).save(any());
    }

    @Test
    @DisplayName("repoFullName이 중복이면 ConflictError(407)를 반환하고 저장하지 않는다")
    void AddProject_duplicateRepoFullName_returnsConflict407_andNoSideEffect() {
        // Given
        long ownerUserId = 10L;
        String repoFullName = "org/repo";

        var owner = aUser(ownerUserId);
        AddProjectCommand cmd = new AddProjectCommand(ownerUserId, "demo", repoFullName);

        when(userRepository.findById(ownerUserId)).thenReturn(Optional.of(owner));

        // 구현이 existsByRepoFullName을 쓰면 이 라인 사용
        when(projectRepository.existsByRepoFullName(repoFullName)).thenReturn(true);

        // 구현이 findByRepoFullName을 쓰면 위 라인 대신 아래 사용
        // when(projectRepository.findByRepoFullName(repoFullName))
        //        .thenReturn(Optional.of(aProject(1L, owner)));

        // When
        Result result = sut.AddProject(cmd);

        // Then
        assertTrue(result.isFailure());
        assertFalse(result.getErrors().isEmpty());
        assertTrue(result.getValue().isEmpty());

        var err = (ResultError) result.getErrors().getFirst();

        // ✅ 네 ConflictError가 407로 고정
        assertEquals(407, err.getCode());
        assertNotNull(err.getMessage());
        assertFalse(err.getMessage().isBlank());

        verify(projectRepository, never()).save(any());
        verify(projectUserRepository, never()).save(any());
    }

    // -----------------------
    // fixture helpers
    // -----------------------

    private UserEntity aUser(long id) {
        // 핵심 필드는 통제(랜덤 금지) :contentReference[oaicite:14]{index=14}
        return fixtureMonkey.giveMeBuilder(UserEntity.class)
                .set("id", id)
                .set("githubId", 1000L + id)
                .set("githubLogin", "user" + id)
                .set("name", "name" + id)
                .sample();
    }

    private ProjectEntity aProject(long projectId, UserEntity owner) {
        return fixtureMonkey.giveMeBuilder(ProjectEntity.class)
                .set("id", projectId)
                .set("name", "project-" + projectId)
                .set("owner", owner)
                .sample();
    }

    private ProjectUserEntity aProjectUser(UserEntity user, ProjectEntity project) {
        return fixtureMonkey.giveMeBuilder(ProjectUserEntity.class)
                .set("user", user)
                .set("project", project)
                .set("role", ProjectUserRole.MEMBER)
                .set("status", ProjectUserStatus.ACTIVE)
                .sample();
    }

    private Long idOf(ProjectEntity project) {
        // 엔티티에 getId()가 있으면 그냥 project.getId()로 바꿔도 됨
        try {
            var f = ProjectEntity.class.getDeclaredField("id");
            f.setAccessible(true);
            return (Long) f.get(project);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
