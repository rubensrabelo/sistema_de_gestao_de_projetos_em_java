package com.management.project.data.dto.project;

import com.management.project.model.enums.StatusEnum;

import java.time.Instant;

public record ProjectOutputDto(
        Long id,
        String name,
        StatusEnum status,
        Instant createdAt,
        Instant updatedAt
) {
}
