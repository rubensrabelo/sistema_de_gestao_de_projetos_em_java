package com.management.project.data.dto.task;

public record TaskCollaboratorCountDTO(
        Long taskId,
        String taskName,
        Long collaboratorCount
) {}
