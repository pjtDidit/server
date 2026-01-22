package com.didit.server.data.repository;

import com.didit.server.data.entity.ProjectUserEntity;
import com.didit.server.data.entity.UserEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProjectUserRepository extends JpaRepository<ProjectUserEntity, Long> {

    List<ProjectUserEntity> findAllByUser_Id(long userid);

    Optional<ProjectUserEntity> findByProject_IdAndUser_Id(long projectId, long userId);

    List<ProjectUserEntity> findAllByProject_Id(long projectId);

    @Query("""
        select pu.user
        from ProjectUserEntity pu
        where pu.project.id = :projectId
        """)
    List<UserEntity> findUsersByProjectId(@Param("projectId") long projectId);
}
