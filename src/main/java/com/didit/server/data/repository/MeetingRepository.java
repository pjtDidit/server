package com.didit.server.data.repository;

import com.didit.server.data.entity.MeetingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingRepository extends JpaRepository<MeetingEntity, Long> {
}
