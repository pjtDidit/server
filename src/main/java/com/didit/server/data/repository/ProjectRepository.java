package com.didit.server.data.repository;

import com.didit.server.data.entity.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<ProjectEntity, Long> {

    boolean existsByRepoFullName(String repoFullName);
}
