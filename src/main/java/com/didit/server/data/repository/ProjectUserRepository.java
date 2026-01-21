package com.didit.server.data.repository;

import com.didit.server.data.entity.ProjectUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectUserRepository extends JpaRepository<ProjectUserEntity, Long> {
}
