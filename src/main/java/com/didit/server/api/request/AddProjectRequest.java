package com.didit.server.api.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

public record AddProjectRequest(
        @NotNull
        @Size(max=64)
        String projectName,
        @NotNull
        @URL
        String githubUrl
) {
}
