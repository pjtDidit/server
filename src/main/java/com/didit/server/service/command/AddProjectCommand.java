package com.didit.server.service.command;

public record AddProjectCommand(long ownerUserId, String projectName, String repoFullName) {

}
