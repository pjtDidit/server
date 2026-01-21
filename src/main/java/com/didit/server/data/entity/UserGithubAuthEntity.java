package com.didit.server.data.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_github_auth")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserGithubAuthEntity {

    @Id
    @Column(name = "user_id")
    private Long userId;

    // users.id (PK/FK)
    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_user_github_auth_user_id")
    )
    private UserEntity user;

    @Lob
    @Column(name = "access_token", nullable = false, columnDefinition = "TEXT")
    private String accessToken;

    @Column(name = "scope", length = 255)
    private String scope;

    @Column(name = "token_updated_at")
    private LocalDateTime tokenUpdatedAt;
}
