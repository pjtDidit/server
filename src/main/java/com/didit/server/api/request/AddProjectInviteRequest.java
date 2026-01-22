package com.didit.server.api.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record AddProjectInviteRequest(
        @NotNull
        long projectId,
        LocalDateTime expireDate
) {
}
