package com.management.project.data.dto.collaborator;

public record CollaboratorTaskCount(
        Long collaboratorId,
        String collaboratorName,
        Long taskCount
) {}
