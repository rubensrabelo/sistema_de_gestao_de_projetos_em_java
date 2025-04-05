package com.management.project.data.dto.project;

public record ProjectTaskCountDTO(
        Long projectId,
        String projectName,
        Long taskCount
) {}
