package com.didit.server.data.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.*;
import org.hibernate.generator.EventType;
import org.hibernate.type.SqlTypes;
import org.hibernate.type.descriptor.jdbc.TinyIntJdbcType;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "projects",
        indexes = {
                @Index(name = "idx_projects_repo_id", columnList = "repo_id"),
                @Index(name = "uq_projects_repo_active", columnList = "repo_full_name, active_key", unique = true)
        }
)
@SQLDelete(sql = "UPDATE projects SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    // users.id (FK)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "owner_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_projects_owner_id")
    )
    private UserEntity owner;

    @Column(name = "repo_id")
    private Long repoId;

    @Column(name = "repo_full_name", length = 255)
    private String repoFullName;

    @Column(name = "thumbnail_url", length = 500)
    private String thumbnailUrl;

    @Generated(event = EventType.UPDATE)
    @Column(name = "active_key", insertable = false, updatable = false)
    @JdbcTypeCode(SqlTypes.TINYINT)
    private Integer activeKey;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // soft delete
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
