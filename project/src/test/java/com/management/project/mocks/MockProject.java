package com.management.project.mocks;

import com.management.project.data.dto.project.ProjectResponseDTO;
import com.management.project.model.Project;
import com.management.project.model.enums.StatusEnum;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class MockProject {

    public Project mockEntity(int size) {
        return createMockEntity(size);
    }

    public List<Project> mockListEntity(int size) {
        return createMockListEntity(size);
    }

    public ProjectResponseDTO mockDTO(int size) {
        return createMockDTO(size);
    }

    public List<ProjectResponseDTO> mockListDTO(int size) {
        return createMockListDTO(size);
    }

    private Project createMockEntity(int size) {
        Project project = new Project();

        project.setId((long) size);
        project.setName("Project " + size);

        StatusEnum status = createStatus(size);
        project.setStatus(status);

        project.setCreatedAt(Instant.now());
        project.setUpdatedAt(Instant.now());

        return project;
    }

    private List<Project> createMockListEntity(int size) {
        List<Project> projects = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            Project project = createMockEntity(size);
            projects.add(project);
        }

        return projects;
    }

    private ProjectResponseDTO createMockDTO(int size) {
        ProjectResponseDTO projectDTO = new ProjectResponseDTO();

        projectDTO.setId((long) size);
        projectDTO.setName("projectDTO " + size);

        StatusEnum status = createStatus(size);
        projectDTO.setStatus(status);

        projectDTO.setCreatedAt(Instant.now());
        projectDTO.setUpdatedAt(Instant.now());

        return projectDTO;
    }

    private List<ProjectResponseDTO> createMockListDTO(int size) {
        List<ProjectResponseDTO> projectDTOs = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            ProjectResponseDTO projectDTO = createMockDTO(size);
            projectDTOs.add(projectDTO);
        }

        return projectDTOs;
    }

    private StatusEnum createStatus(int num) {
        StatusEnum status = StatusEnum.NOT_DONE;

        if(num % 2 == 0) {
            status = StatusEnum.DOING;
        } else if(num % 5 == 0) {
            status = StatusEnum.DONE;
        }

        return status;
    }
}
