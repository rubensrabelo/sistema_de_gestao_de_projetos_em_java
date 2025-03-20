package com.management.project.data.dto.project;

import com.management.project.model.enums.StatusEnum;
import jakarta.validation.constraints.Size;


public class ProjectUpdateDTO {
    @Size(min = 3, max = 100)
    private String name;
    private StatusEnum status;

    public ProjectUpdateDTO() {}

    public ProjectUpdateDTO(String name, StatusEnum status) {
        this.name = name;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public StatusEnum getStatus() {
        return status;
    }

    public void setStatus(StatusEnum status) {
        this.status = status;
    }
}
