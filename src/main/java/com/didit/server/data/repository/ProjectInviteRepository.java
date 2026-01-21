package com.didit.server.data.repository;

import com.didit.server.data.entity.ProjectInviteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectInviteRepository extends JpaRepository<ProjectInviteEntity, String> {
}
