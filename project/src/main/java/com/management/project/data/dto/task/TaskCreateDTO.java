package com.management.project.data.dto.task;

import com.management.project.model.enums.StatusEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public class TaskCreateDTO {

    @NotBlank
    @Size(min = 3, max = 100)
    private String name;
    private StatusEnum status;

    @NotNull
    private Long projectId;

    public TaskCreateDTO() {}

    public TaskCreateDTO(String name, StatusEnum status, Long projectId) {
        this.name = name;
        this.status = status;
        this.projectId = projectId;
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

    public Long getProjectId() {
        return this.projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }
}
