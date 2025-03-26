package com.management.project.unittest.service;

import com.management.project.data.dto.task.TaskResponseDTO;
import com.management.project.model.Task;
import com.management.project.model.enums.StatusEnum;
import com.management.project.repository.ProjectRepository;
import com.management.project.repository.TaskRepository;
import com.management.project.service.TaskService;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
    void create() {
    }

    @Test
    void assignCollaboratorToTask() {
    }

    @Test
    void update() {
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