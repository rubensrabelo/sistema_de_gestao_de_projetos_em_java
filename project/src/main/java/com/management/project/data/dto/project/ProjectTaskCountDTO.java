package com.management.project.data.dto.project;

public class ProjectTaskCountDTO {
    private Long projectId;
    private String projectName;
    private Long taskCount;

    public ProjectTaskCountDTO() {}

    public ProjectTaskCountDTO(Long projectId, String projectName, Long taskCount) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.taskCount = taskCount;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Long getTaskCount() {
        return taskCount;
    }

    public void setTaskCount(Long taskCount) {
        this.taskCount = taskCount;
    }
}
