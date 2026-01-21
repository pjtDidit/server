-- V1__init.sql
-- MySQL 8.x / InnoDB / utf8mb4
-- created_at, updated_at => TIMESTAMP 로 통일

-- =========================
-- 1) USERS / AUTH
-- =========================

CREATE TABLE users (
                       id            BIGINT       NOT NULL AUTO_INCREMENT,
                       github_id     BIGINT       NOT NULL,
                       github_login  VARCHAR(100) NOT NULL,
                       name          VARCHAR(100) NULL,
                       avatar_url    VARCHAR(500) NULL,
                       created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       last_login_at DATETIME     NULL,

                       PRIMARY KEY (id),
                       UNIQUE KEY uk_users_github_id (github_id),
                       UNIQUE KEY uk_users_github_login (github_login)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE user_github_auth (
                                  user_id          BIGINT       NOT NULL,
                                  access_token     TEXT         NOT NULL,
                                  scope            VARCHAR(255) NULL,
                                  token_updated_at DATETIME     NULL,

                                  PRIMARY KEY (user_id),
                                  CONSTRAINT fk_user_github_auth_user
                                      FOREIGN KEY (user_id) REFERENCES users(id)
                                          ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =========================
-- 2) PROJECTS / MEMBERSHIP / INVITES
-- =========================

CREATE TABLE projects (
                          id             BIGINT        NOT NULL AUTO_INCREMENT,
                          name           VARCHAR(100)  NOT NULL,
                          owner_id       BIGINT        NOT NULL,
                          repo_id        BIGINT        NULL,
                          repo_full_name VARCHAR(255)  NULL,
                          thumbnail_url  VARCHAR(500)  NULL,
                          created_at     TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at     TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                          deleted_at     DATETIME      NULL,

                          PRIMARY KEY (id),
                          UNIQUE KEY uk_projects_repo_full_name (repo_full_name),
                          KEY idx_projects_owner_id (owner_id),
                          KEY idx_projects_repo_id (repo_id),
                          CONSTRAINT fk_projects_owner
                              FOREIGN KEY (owner_id) REFERENCES users(id)
                                  ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE project_users (
                               id         BIGINT      NOT NULL AUTO_INCREMENT,
                               project_id BIGINT      NOT NULL,
                               user_id    BIGINT      NOT NULL,
                               role       ENUM('ADMIN','MEMBER')   NOT NULL,
                               status     ENUM('PENDING','ACTIVE') NOT NULL,
                               joined_at  DATETIME    NULL,
                               left_at    DATETIME    NULL,
                               created_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               updated_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                               PRIMARY KEY (id),
                               UNIQUE KEY uk_project_users_project_user (project_id, user_id),
                               KEY idx_project_users_project_id (project_id),
                               KEY idx_project_users_user_id (user_id),
                               CONSTRAINT fk_project_users_project
                                   FOREIGN KEY (project_id) REFERENCES projects(id)
                                       ON DELETE CASCADE,
                               CONSTRAINT fk_project_users_user
                                   FOREIGN KEY (user_id) REFERENCES users(id)
                                       ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 초대 링크(UUID)
CREATE TABLE project_invites (
                                 id         CHAR(36)    CHARACTER SET ascii NOT NULL,
                                 project_id BIGINT      NOT NULL,
                                 expires_at DATETIME    NOT NULL,
                                 created_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 updated_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                                 PRIMARY KEY (id),
                                 KEY idx_project_invites_project_id (project_id),
                                 KEY idx_project_invites_expires_at (expires_at),
                                 CONSTRAINT fk_project_invites_project
                                     FOREIGN KEY (project_id) REFERENCES projects(id)
                                         ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =========================
-- 3) MEETINGS / ATTENDANCE / SPEECH / SUMMARY
-- =========================

CREATE TABLE meetings (
                          id         BIGINT      NOT NULL AUTO_INCREMENT,
                          project_id BIGINT      NOT NULL,
                          created_by BIGINT      NOT NULL,
                          session_id TEXT        NOT NULL,
                          title      VARCHAR(50) NOT NULL,
                          status     ENUM('SCHEDULED','RUNNING','ENDED') NOT NULL,
                          started_at DATETIME    NULL,
                          ended_at   DATETIME    NULL,
                          created_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                          PRIMARY KEY (id),
                          KEY idx_meetings_project_id (project_id),
                          KEY idx_meetings_created_by (created_by),
                          CONSTRAINT fk_meetings_project
                              FOREIGN KEY (project_id) REFERENCES projects(id)
                                  ON DELETE CASCADE,
                          CONSTRAINT fk_meetings_creator
                              FOREIGN KEY (created_by) REFERENCES users(id)
                                  ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE meeting_users (
                               id         BIGINT    NOT NULL AUTO_INCREMENT,
                               meeting_id BIGINT    NOT NULL,
                               user_id    BIGINT    NOT NULL,
                               joined_at  DATETIME  NULL,
                               left_at    DATETIME  NULL,
                               created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                               PRIMARY KEY (id),
                               UNIQUE KEY uk_meeting_users_meeting_user (meeting_id, user_id),
                               KEY idx_meeting_users_meeting_id (meeting_id),
                               KEY idx_meeting_users_user_id (user_id),
                               CONSTRAINT fk_meeting_users_meeting
                                   FOREIGN KEY (meeting_id) REFERENCES meetings(id)
                                       ON DELETE CASCADE,
                               CONSTRAINT fk_meeting_users_user
                                   FOREIGN KEY (user_id) REFERENCES users(id)
                                       ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE meeting_speech_scripts (
                                        id              BIGINT    NOT NULL AUTO_INCREMENT,
                                        meeting_id      BIGINT    NOT NULL,
                                        speaker_user_id BIGINT    NULL,
                                        script          TEXT      NOT NULL,
                                        started_at      DATETIME  NULL,
                                        created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                        updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                                        PRIMARY KEY (id),
                                        KEY idx_meeting_speech_scripts_meeting_id (meeting_id),
                                        KEY idx_meeting_speech_scripts_speaker_user_id (speaker_user_id),
                                        CONSTRAINT fk_mss_meeting
                                            FOREIGN KEY (meeting_id) REFERENCES meetings(id)
                                                ON DELETE CASCADE,
                                        CONSTRAINT fk_mss_speaker_user
                                            FOREIGN KEY (speaker_user_id) REFERENCES users(id)
                                                ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE meeting_summary (
                                 id           BIGINT    NOT NULL AUTO_INCREMENT,
                                 meeting_id   BIGINT    NOT NULL,
                                 summary_md   TEXT      NOT NULL,
                                 published_at DATETIME  NULL,
                                 version      INT       NOT NULL,
                                 edited_by    BIGINT    NULL,
                                 generated_at DATETIME  NOT NULL,
                                 created_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 updated_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                                 PRIMARY KEY (id),
                                 UNIQUE KEY uk_meeting_summary_meeting_version (meeting_id, version),
                                 KEY idx_meeting_summary_meeting_id (meeting_id),
                                 KEY idx_meeting_summary_edited_by (edited_by),
                                 CONSTRAINT fk_meeting_summary_meeting
                                     FOREIGN KEY (meeting_id) REFERENCES meetings(id)
                                         ON DELETE CASCADE,
                                 CONSTRAINT fk_meeting_summary_edited_by
                                     FOREIGN KEY (edited_by) REFERENCES users(id)
                                         ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =========================
-- 4) ISSUES (GitHub 연동)
-- =========================

CREATE TABLE issues (
                        id              BIGINT       NOT NULL AUTO_INCREMENT,
                        project_id      BIGINT       NOT NULL,
                        github_issue_id BIGINT       NOT NULL,
                        issue_no        INT          NOT NULL,
                        title           VARCHAR(255) NOT NULL,
                        body            TEXT         NULL,
                        status          ENUM('OPEN','CLOSED') NOT NULL,
                        priority        ENUM('HIGH','MEDIUM','LOW') NOT NULL,
                        author_id       BIGINT       NOT NULL,
                        created_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        updated_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                        closed_at       DATETIME     NULL,

                        PRIMARY KEY (id),
                        UNIQUE KEY uk_issues_project_github_issue_id (project_id, github_issue_id),
                        KEY idx_issues_project_id (project_id),
                        KEY idx_issues_github_issue_id (github_issue_id),
                        KEY idx_issues_issue_no (issue_no),
                        KEY idx_issues_author_id (author_id),
                        CONSTRAINT fk_issues_project
                            FOREIGN KEY (project_id) REFERENCES projects(id)
                                ON DELETE CASCADE,
                        CONSTRAINT fk_issues_author
                            FOREIGN KEY (author_id) REFERENCES users(id)
                                ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE issue_assignees (
                                 id         BIGINT    NOT NULL AUTO_INCREMENT,
                                 issue_id   BIGINT    NOT NULL,
                                 user_id    BIGINT    NOT NULL,
                                 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                                 PRIMARY KEY (id),
                                 UNIQUE KEY uk_issue_assignees_issue_user (issue_id, user_id),
                                 KEY idx_issue_assignees_issue_id (issue_id),
                                 KEY idx_issue_assignees_user_id (user_id),
                                 CONSTRAINT fk_issue_assignees_issue
                                     FOREIGN KEY (issue_id) REFERENCES issues(id)
                                         ON DELETE CASCADE,
                                 CONSTRAINT fk_issue_assignees_user
                                     FOREIGN KEY (user_id) REFERENCES users(id)
                                         ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =========================
-- 5) CHATS / READ STATE
-- =========================

CREATE TABLE chats (
                       id           BIGINT    NOT NULL AUTO_INCREMENT,
                       project_id   BIGINT    NOT NULL,
                       meeting_id   BIGINT    NULL,
                       user_id      BIGINT    NOT NULL,
                       message_type ENUM('TEXT','SYSTEM') NOT NULL,
                       message      TEXT      NOT NULL,
                       created_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       edited_at    DATETIME  NULL,
                       deleted_at   DATETIME  NULL,

                       PRIMARY KEY (id),
                       KEY idx_chats_project_created_at (project_id, created_at),
                       KEY idx_chats_meeting_created_at (meeting_id, created_at),
                       KEY idx_chats_user_id (user_id),
                       CONSTRAINT fk_chats_project
                           FOREIGN KEY (project_id) REFERENCES projects(id)
                               ON DELETE CASCADE,
                       CONSTRAINT fk_chats_meeting
                           FOREIGN KEY (meeting_id) REFERENCES meetings(id)
                               ON DELETE SET NULL,
                       CONSTRAINT fk_chats_user
                           FOREIGN KEY (user_id) REFERENCES users(id)
                               ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE project_user_reads (
                                    id               BIGINT    NOT NULL AUTO_INCREMENT,
                                    project_id       BIGINT    NOT NULL,
                                    user_id          BIGINT    NOT NULL,
                                    last_read_chat_id BIGINT   NULL,
                                    updated_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                                    PRIMARY KEY (id),
                                    UNIQUE KEY uk_project_user_reads_project_user (project_id, user_id),
                                    KEY idx_project_user_reads_project_id (project_id),
                                    KEY idx_project_user_reads_user_id (user_id),
                                    KEY idx_project_user_reads_last_read_chat_id (last_read_chat_id),
                                    CONSTRAINT fk_project_user_reads_project
                                        FOREIGN KEY (project_id) REFERENCES projects(id)
                                            ON DELETE CASCADE,
                                    CONSTRAINT fk_project_user_reads_user
                                        FOREIGN KEY (user_id) REFERENCES users(id)
                                            ON DELETE RESTRICT,
                                    CONSTRAINT fk_project_user_reads_last_chat
                                        FOREIGN KEY (last_read_chat_id) REFERENCES chats(id)
                                            ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
