package com.management.project.service;

import com.management.project.controller.TaskController;
import com.management.project.data.dto.collaborator_task.CollaboratorTaskDTO;
import com.management.project.data.dto.project.ProjectResponseDTO;
import com.management.project.data.dto.task.TaskCollaboratorCountDTO;
import com.management.project.data.dto.task.TaskCreateDTO;
import com.management.project.data.dto.task.TaskResponseDTO;
import com.management.project.data.dto.task.TaskUpdateDTO;
import com.management.project.model.Collaborator;
import com.management.project.model.Project;
import com.management.project.model.Task;
import com.management.project.repository.CollaboratorRepository;
import com.management.project.repository.ProjectRepository;
import com.management.project.repository.TaskRepository;
import com.management.project.service.exceptions.*;
import jakarta.persistence.EntityManager;
import org.modelmapper.ModelMapper;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@Service
public class TaskService {

    private TaskRepository taskRepository;
    private ProjectRepository projectRepository;
    private CollaboratorRepository collaboratorRepository;
    private ModelMapper modelMapper;
    PagedResourcesAssembler<ProjectResponseDTO> assembler;
    private EntityManager entityManager;

    public TaskService(
            TaskRepository taskRepository,
            ProjectRepository projectRepository,
            CollaboratorRepository collaboratorRepository,
            ModelMapper modelMapper,
            PagedResourcesAssembler<ProjectResponseDTO> assembler
    ) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.collaboratorRepository = collaboratorRepository;
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

    public List<TaskCollaboratorCountDTO> countCollaboratorsPerTaskByProjectId(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        return taskRepository.countCollaboratorsPerTaskByProjectId(projectId);
    }

   public TaskResponseDTO create(TaskCreateDTO dto) {
       if(dto == null)
           throw new RequiredObjectIsNullException();

        if(dto.getProjectId() == null) {
            throw new NullForeignKeyException("Project id is required");
        }

        Project project = projectRepository.findById(dto.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        if(dto.getName() == null || dto.getName().isEmpty())
            throw new EmptyNameException("The name task cannot be null or blank.");

        if(dto.getName().length() < 3 || dto.getName().length() > 100)
            throw new InvalidNameSizeException("The name field must be between 3 and 100 characters.");

        Task entity = new Task(dto.getName(), dto.getStatus(), project);

        taskRepository.save(entity);

        TaskResponseDTO dtoResponse = modelMapper.map(entity, TaskResponseDTO.class);
        addHateoasLinks(dtoResponse);

        return dtoResponse;
    }

    public Map<String, String> assignCollaboratorToTask(CollaboratorTaskDTO dto) {
        Task task = taskRepository.findById(dto.getTaskId())
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        Collaborator collaborator = collaboratorRepository.findById(dto.getCollaboratorId())
                .orElseThrow(() -> new ResourceNotFoundException("Collaborator not found"));

        if (task.getCollaborators().contains(collaborator)) {
            throw new DuplicateAssignmentException("Collaborator is already assigned to this task.");
        }

        task.addCollaborators(collaborator);
        taskRepository.save(task);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Collaborator: " + collaborator.getName() + " assigned to Task: " + task.getName());

        return response;
    }

    public TaskResponseDTO update(Long id, TaskUpdateDTO updatedData) {
        if(updatedData == null)
            throw new RequiredObjectIsNullException();

        Task entity = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        updateData(entity, updatedData);
        taskRepository.save(entity);

        TaskResponseDTO dtoResponse = modelMapper.map(entity, TaskResponseDTO.class);
        addHateoasLinks(dtoResponse);

        return dtoResponse;
    }

    private void updateData(Task entity, TaskUpdateDTO updatedData) {
        if(updatedData.getName() != null) {
            if(updatedData.getName().length() < 3 || updatedData.getName().length() > 100)
                throw new InvalidNameSizeException("The name must be between 3 and 100 characters.");
            entity.setName(updatedData.getName());
        }
        if(updatedData.getStatus() != null)
            entity.setStatus(updatedData.getStatus());
    }

    private void addHateoasLinks(TaskResponseDTO dto) {
        dto.add(linkTo(methodOn(TaskController.class).findById(dto.getId())).withSelfRel().withType("GET"));

        TaskCreateDTO dtoCreated = new TaskCreateDTO(dto.getName(), dto.getStatus(), dto.getProjectId());
        dto.add(linkTo(methodOn(TaskController.class).create(dtoCreated)).withRel("create").withType("POST"));

        TaskUpdateDTO dtoUpdated = new TaskUpdateDTO(dto.getName(), dto.getStatus());
        dto.add(linkTo(methodOn(TaskController.class).update(dto.getId(), dtoUpdated)).withRel("update").withType("PUT"));
    }
}
