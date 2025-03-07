package com.management.project.data.dto.project;

import com.management.project.model.enums.StatusEnum;

import javax.validation.constraints.Size;

public record ProjectUpdateDTO(
        @Size(min = 3, max = 100) String name,
        StatusEnum Status
) {}
