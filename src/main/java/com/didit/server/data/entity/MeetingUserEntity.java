package com.didit.server.data.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "meeting_users",
        uniqueConstraints = @UniqueConstraint(name = "uk_meeting_users_meeting_user", columnNames = {"meeting_id", "user_id"}),
        indexes = {
                @Index(name = "idx_meeting_users_meeting_id", columnList = "meeting_id"),
                @Index(name = "idx_meeting_users_user_id", columnList = "user_id")
        }
)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MeetingUserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "meeting_id", nullable = false, foreignKey = @ForeignKey(name = "fk_meeting_users_meeting"))
    private MeetingEntity meeting;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_meeting_users_user"))
    private UserEntity user;

    @Column(name = "joined_at")
    private LocalDateTime joinedAt;

    @Column(name = "left_at")
    private LocalDateTime leftAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}