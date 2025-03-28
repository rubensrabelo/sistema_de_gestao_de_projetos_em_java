package com.management.project.unittest.service;

import com.management.project.data.dto.collaborator.CollaboratorCreateDTO;
import com.management.project.data.dto.collaborator.CollaboratorResponseDTO;
import com.management.project.data.dto.collaborator_task.CollaboratorTaskDTO;
import com.management.project.data.dto.task.TaskCreateDTO;
import com.management.project.data.dto.task.TaskResponseDTO;
import com.management.project.data.dto.task.TaskUpdateDTO;
import com.management.project.model.Collaborator;
import com.management.project.model.Project;
import com.management.project.model.Task;
import com.management.project.model.enums.FunctionEnum;
import com.management.project.model.enums.StatusEnum;
import com.management.project.repository.CollaboratorRepository;
import com.management.project.repository.ProjectRepository;
import com.management.project.repository.TaskRepository;
import com.management.project.service.TaskService;
import com.management.project.service.exceptions.ResourceNotFoundException;
import com.management.project.unittest.mocks.MockTask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Falta fazer os testes de execeções

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    MockTask input;

    @InjectMocks
    private TaskService taskService;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private CollaboratorRepository collaboratorRepository;

    @Mock
    private ModelMapper modelMapper;

    @BeforeEach
    void setUp() {
        input = new MockTask();
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findById() {
        Task entity  = input.mockEntity(1);
        TaskResponseDTO dtoResponse = input.mockDTO(1);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(modelMapper.map(entity, TaskResponseDTO.class)).thenReturn(dtoResponse);

        TaskResponseDTO result = taskService.findById(1L);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getLinks());

        assertEquals("TaskDTO 1", result.getName());
        assertEquals(StatusEnum.NOT_DONE, result.getStatus());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());

        assertLinkExists(result, "self", "/v1/tasks/" + dtoResponse.getId(), "GET");
        assertLinkExists(result, "create", "/v1/tasks", "POST");
        assertLinkExists(result, "update", "/v1/tasks/" + dtoResponse.getId(), "PUT");

        verify(taskRepository, times(1)).findById(1L);
        verify(modelMapper, times(1)).map(entity, TaskResponseDTO.class);
    }

    @Test
    void testFindByIdWithIdDoesNotExist() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(
                ResourceNotFoundException.class,
                () -> taskService.findById(1L)
        );

        String expectedMessage = "Task not found";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
        verify(taskRepository, times(1)).findById(1L);
        verifyNoInteractions(modelMapper);
    }

    @Test
    void create() {
        TaskCreateDTO dtoCreate = new TaskCreateDTO("New Task", StatusEnum.DOING, 1L);
        Task entity = new Task("New Task", StatusEnum.DOING, new Project());
        TaskResponseDTO dtoResponse = new TaskResponseDTO();
        dtoResponse.setId(1L);
        dtoResponse.setName("New Task");
        dtoResponse.setStatus(StatusEnum.DOING);
        dtoResponse.setCreatedAt(Instant.now());
        dtoResponse.setUpdatedAt(Instant.now());


        when(projectRepository.findById(1L)).thenReturn(Optional.of(new Project()));
        when(taskRepository.save(any(Task.class))).thenReturn(entity);
        when(modelMapper.map(entity, TaskResponseDTO.class)).thenReturn(dtoResponse);

        TaskResponseDTO result = taskService.create(dtoCreate);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getLinks());

        assertEquals("New Task", result.getName());
        assertEquals(StatusEnum.DOING, result.getStatus());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());

        assertLinkExists(result, "self", "/v1/tasks/" + dtoResponse.getId(), "GET");
        assertLinkExists(result, "create", "/v1/tasks", "POST");
        assertLinkExists(result, "update", "/v1/tasks/" + dtoResponse.getId(), "PUT");

        verify(projectRepository, times(1)).findById(1L);
        verify(taskRepository, times(1)).save(entity);
        verify(modelMapper, times(1)).map(entity, TaskResponseDTO.class);
    }

    @Test
    void assignCollaboratorToTask() {
        Task entityTask = input.mockEntity(1);
        Collaborator entityCollaborator = new Collaborator(
                "Collaborator 1",
                "test@test.com",
                FunctionEnum.DEVELOPER
        );
        entityCollaborator.setId(1L);
        CollaboratorTaskDTO dtoResponse = new CollaboratorTaskDTO(1L, 1L);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(entityTask));
        when(collaboratorRepository.findById(1L)).thenReturn(Optional.of(entityCollaborator));

        Map<String, String> response = taskService.assignCollaboratorToTask(dtoResponse);

        assertEquals("Collaborator: Collaborator 1 assigned to Task: Task 1", response.get("message"));
        assertTrue(entityTask.getCollaborators().contains(entityCollaborator));

        verify(taskRepository).save(entityTask);
        verify(taskRepository, times(1)).findById(1L);
        verify(collaboratorRepository, times(1)).findById(1L);
    }

    @Test
    void update() {
        TaskUpdateDTO updateDTO = new TaskUpdateDTO("Updated Task", StatusEnum.NOT_DONE);
        Task entity = input.mockEntity(1);
        TaskResponseDTO dtoResponse = input.mockDTO(1);
        entity.setName("Updated Task");

        when(taskRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(taskRepository.save(any(Task.class))).thenReturn(entity);
        when(modelMapper.map(entity, TaskResponseDTO.class)).thenReturn(dtoResponse);

        TaskResponseDTO result = taskService.update(1L, updateDTO);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getLinks());

        assertEquals("TaskDTO 1", result.getName());
        assertEquals(StatusEnum.NOT_DONE, result.getStatus());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());

        assertLinkExists(result, "self", "/v1/tasks/" + dtoResponse.getId(), "GET");
        assertLinkExists(result, "create", "/v1/tasks", "POST");
        assertLinkExists(result, "update", "/v1/tasks/" + dtoResponse.getId(), "PUT");

        verify(taskRepository, times(1)).findById(1L);
        verify(taskRepository, times(1)).save(entity);
        verify(modelMapper, times(1)).map(entity, TaskResponseDTO.class);
    }

    private void assertLinkExists(TaskResponseDTO dto, String rel, String href, String type) {
        boolean linkExists = dto.getLinks().stream()
                .anyMatch(link ->
                        link.getRel().value().equals(rel) &&
                                link.getHref().endsWith(href) &&
                                link.getType().equals(type)
                );

        assertTrue(linkExists, "O link '" + rel + "' com href '" + href + "' e type '" + type + "' não foi encontrado.");
    }
}