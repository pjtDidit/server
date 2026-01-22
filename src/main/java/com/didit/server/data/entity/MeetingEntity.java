package com.didit.server.data.entity;

import com.didit.server.data.entity.enums.MeetingMode;
import com.didit.server.data.entity.enums.MeetingStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "meetings", indexes = {
        @Index(name = "idx_meetings_project_id", columnList = "project_id"),
        @Index(name = "idx_meetings_created_by", columnList = "created_by")
})
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MeetingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false, foreignKey = @ForeignKey(name = "fk_meetings_project"))
    private ProjectEntity project;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by", nullable = false, foreignKey = @ForeignKey(name = "fk_meetings_creator"))
    private UserEntity createdBy;

    @Column(name = "session_id", nullable = false, columnDefinition = "TEXT")
    private String sessionId;

    @Column(name = "title", nullable = false, length = 50)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private MeetingStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "mode", nullable = false)
    private MeetingMode mode;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}