package com.management.project.data.dto.collaborator_task;

import jakarta.validation.constraints.NotNull;

public class CollaboratorTaskDTO {

    @NotNull
    private Long taskId;

    @NotNull
    private Long collaboratorId;

    public CollaboratorTaskDTO() {}

    public CollaboratorTaskDTO(Long taskId, Long collaboratorId) {
        this.taskId = taskId;
        this.collaboratorId = collaboratorId;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getCollaboratorId() {
        return collaboratorId;
    }

    public void setCollaboratorId(Long collaboratorId) {
        this.collaboratorId = collaboratorId;
    }
}
