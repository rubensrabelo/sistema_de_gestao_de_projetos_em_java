package com.management.project.service;

import com.management.project.controller.ProjectController;
import com.management.project.controller.TaskController;
import com.management.project.data.dto.project.ProjectCreateDTO;
import com.management.project.data.dto.project.ProjectResponseDTO;
import com.management.project.data.dto.project.ProjectUpdateDTO;
import com.management.project.data.dto.task.TaskCreateDTO;
import com.management.project.data.dto.task.TaskResponseDTO;
import com.management.project.data.dto.task.TaskUpdateDTO;
import com.management.project.model.Project;
import com.management.project.model.Task;
import com.management.project.repository.ProjectRepository;
import com.management.project.repository.TaskRepository;
import com.management.project.service.exceptions.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Service;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@Service
public class TaskService {

    private TaskRepository taskRepository;
    private ProjectRepository projectRepository;
    private ModelMapper modelMapper;
    PagedResourcesAssembler<ProjectResponseDTO> assembler;

    public TaskService(
            TaskRepository taskRepository,
            ProjectRepository projectRepository,
            ModelMapper modelMapper,
            PagedResourcesAssembler<ProjectResponseDTO> assembler
    ) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.modelMapper = modelMapper;
        this.assembler = assembler;
    }

    public TaskResponseDTO findById(Long id) {
        Task entity = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));;

        TaskResponseDTO dtoResponse = modelMapper.map(entity, TaskResponseDTO.class);
        addHateoasLinks(dtoResponse);
        return dtoResponse;
    }

    public TaskResponseDTO create(TaskCreateDTO dto) {
        if(dto.getProjectId() == null) {
            throw new NullForeignKeyException("Project id is required");
        }

        Project project = projectRepository.findById(dto.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        if(dto.getName() == null || dto.getName().isEmpty())
            throw new EmptyNameException("The name task cannot be null or blank.");

        if(dto.getName().length() < 3 || dto.getName().length() > 100)
            throw new InvalidNameSizeException("The name task field must be between 3 and 100 characters.");


        Task entity = modelMapper.map(dto, Task.class);
        entity.setProject(project);

        taskRepository.save(entity);

        ProjectResponseDTO dtoResponse = modelMapper.map(entity, ProjectResponseDTO.class);
        addHateoasLinks(dtoResponse);

        return dtoResponse;
    }

    public ProjectResponseDTO update(Long id, ProjectUpdateDTO updatedData) {
        if(updatedData.getName().length() < 3 || updatedData.getName().length() > 100)
            throw new InvalidNameSizeException("The name task must be between 3 and 100 characters.");

        Task entity = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        Task dataTask = modelMapper.map(updatedData, Task.class);

        updateData(entity, dataTask);
        taskRepository.save(entity);

        ProjectResponseDTO dtoResponse = modelMapper.map(entity, ProjectResponseDTO.class);
        addHateoasLinks(dtoResponse);

        return dtoResponse;
    }

    public void deleteById(Long id) {
        try {
            taskRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException("Task not found");
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    private void updateData(Task entity, Task dataProject) {
        if(dataProject.getName() != null)
            entity.setName(dataProject.getName());
        if(dataProject.getStatus() != null)
            entity.setStatus(dataProject.getStatus());
    }

    private void addHateoasLinks(TaskResponseDTO dto) {
        dto.add(linkTo(methodOn(TaskController.class).findAll(0, 10, "desc")).withRel("findAll").withType("GET"));
        dto.add(linkTo(methodOn(TaskController.class).findById(dto.getId())).withSelfRel().withType("GET"));

        TaskCreateDTO dtoCreated = new TaskCreateDTO(dto.getName(), dto.getStatus(), dto.getProjectId());
        dto.add(linkTo(methodOn(TaskController.class).create(dtoCreated)).withRel("create").withType("POST"));

        TaskUpdateDTO dtoUpdated = new TaskUpdateDTO(dto.getName(), dto.getStatus());
        dto.add(linkTo(methodOn(TaskController.class).update(dto.getId(), dtoUpdated)).withRel("update").withType("PUT"));

        dto.add(linkTo(methodOn(TaskController.class).delete(dto.getId())).withRel("delete").withType("DELETE"));
    }
}
