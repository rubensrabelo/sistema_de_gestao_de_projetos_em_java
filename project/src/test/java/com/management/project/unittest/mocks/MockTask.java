package com.management.project.unittest.mocks;

import com.management.project.controller.CollaboratorController;
import com.management.project.controller.TaskController;
import com.management.project.data.dto.collaborator.CollaboratorCreateDTO;
import com.management.project.data.dto.collaborator.CollaboratorResponseDTO;
import com.management.project.data.dto.collaborator.CollaboratorUpdateDTO;
import com.management.project.data.dto.task.TaskCreateDTO;
import com.management.project.data.dto.task.TaskResponseDTO;
import com.management.project.data.dto.task.TaskUpdateDTO;
import com.management.project.model.Collaborator;
import com.management.project.model.Task;
import com.management.project.model.enums.FunctionEnum;
import com.management.project.model.enums.StatusEnum;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class MockTask {

    public Task mockEntity(int size) {
        return createMockEntity(size);
    }

    public List<Task> mockListEntity(int size) {
        return createMockListEntity(size);
    }

    public TaskResponseDTO mockDTO(int size) {
        return createMockDTO(size);
    }

    public List<TaskResponseDTO> mockListDTO(int size) {
        return createMockListDTO(size);
    }

    private Task createMockEntity(int size) {
        Task task = new Task();

        task.setId((long) size);
        task.setName("Task " + size);

        StatusEnum status = createStatus(size);
        task.setStatus(status);

        task.setCreatedAt(Instant.now());
        task.setUpdatedAt(Instant.now());

        return task;
    }

    private List<Task> createMockListEntity(int size) {
        List<Task> tasks = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            Task task = createMockEntity(i);
            tasks.add(task);
        }

        return tasks;
    }

    private TaskResponseDTO createMockDTO(int size) {
        TaskResponseDTO taskDTO = new TaskResponseDTO();

        taskDTO.setId((long) size);
        taskDTO.setName("TaskDTO " + size);
        StatusEnum status = createStatus(size);
        taskDTO.setStatus(status);
        taskDTO.setCreatedAt(Instant.now());
        taskDTO.setUpdatedAt(Instant.now());

        addHateoasLinks(taskDTO);

        return taskDTO;
    }

    private List<TaskResponseDTO> createMockListDTO(int size) {
        List<TaskResponseDTO> taskDTOs = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            TaskResponseDTO taskDTO = createMockDTO(i);
            taskDTOs.add(taskDTO);
        }

        return taskDTOs;
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

    private void addHateoasLinks(TaskResponseDTO dto) {
        dto.add(linkTo(methodOn(TaskController.class).findById(dto.getId()))
                .withSelfRel().withType("GET"));
        dto.add(linkTo(methodOn(TaskController.class).create(new TaskCreateDTO(dto.getName(), dto.getStatus(), dto.getProjectId())))
                .withRel("create").withType("POST"));
        dto.add(linkTo(methodOn(TaskController.class).update(dto.getId(), new TaskUpdateDTO(dto.getName(), dto.getStatus())))
                .withRel("update").withType("PUT"));
    }
}
