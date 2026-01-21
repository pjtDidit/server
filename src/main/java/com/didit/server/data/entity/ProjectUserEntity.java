package com.didit.server.data.entity;

import com.didit.server.data.entity.enums.ProjectUserRole;
import com.didit.server.data.entity.enums.ProjectUserStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "project_users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_project_users_project_user", columnNames = {"project_id", "user_id"})
        },
        indexes = {
                @Index(name = "idx_project_users_project_id", columnList = "project_id"),
                @Index(name = "idx_project_users_user_id", columnList = "user_id")
        }
)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectUserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // projects.id (FK)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "project_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_project_users_project_id")
    )
    private ProjectEntity project;

    // users.id (FK)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_project_users_user_id")
    )
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 10)
    private ProjectUserRole role;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    private ProjectUserStatus status;

    @Column(name = "joined_at")
    private LocalDateTime joinedAt;

    @Column(name = "left_at")
    private LocalDateTime leftAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
