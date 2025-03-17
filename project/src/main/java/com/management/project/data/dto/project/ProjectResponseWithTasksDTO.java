package com.management.project.data.dto.project;

import com.management.project.data.dto.task.TaskResponseDTO;
import com.management.project.model.enums.StatusEnum;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

public class ProjectResponseWithTasksDTO extends RepresentationModel<ProjectResponseWithTasksDTO> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private StatusEnum status;
    private Instant createdAt;
    private Instant updatedAt;

    private List<TaskResponseDTO> tasks;

    public ProjectResponseWithTasksDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<TaskResponseDTO> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskResponseDTO> tasks) {
        this.tasks = tasks;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ProjectResponseWithTasksDTO that = (ProjectResponseWithTasksDTO) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && status == that.status && Objects.equals(createdAt, that.createdAt) && Objects.equals(updatedAt, that.updatedAt) && Objects.equals(tasks, that.tasks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, name, status, createdAt, updatedAt, tasks);
    }
}
