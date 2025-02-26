package com.management.project.data.dto.project;

import com.management.project.model.enums.StatusEnum;

public record ProjectUpdateDTO(
        String name,
        StatusEnum Status
) {}
