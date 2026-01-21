package com.didit.server.data.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_users_github_id", columnNames = "github_id"),
                @UniqueConstraint(name = "uk_users_github_login", columnNames = "github_login")
        }
)
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // GitHub numeric user id (UK, NOT NULL)
    @Column(name = "github_id", nullable = false)
    private Long githubId;

    // GitHub 로그인 핸들 (UK, NOT NULL)
    @Column(name = "github_login", nullable = false, length = 100)
    private String githubLogin;

    // 표시 이름 (NULL 가능)
    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    // TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // DATETIME NULL
    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

}
