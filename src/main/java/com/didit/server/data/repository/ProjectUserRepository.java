package com.didit.server.data.repository;

import com.didit.server.data.entity.ProjectUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectUserRepository extends JpaRepository<ProjectUserEntity, Long> {

    List<ProjectUserEntity> findAllByUser_Id(long userid);
}
