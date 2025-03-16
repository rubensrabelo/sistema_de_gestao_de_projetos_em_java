package com.management.project.service;

import com.management.project.data.dto.project.ProjectCreateDTO;
import com.management.project.data.dto.project.ProjectResponseDTO;
import com.management.project.mocks.MockProject;
import com.management.project.model.Project;
import com.management.project.model.enums.StatusEnum;
import com.management.project.repository.ProjectRepository;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    MockProject input;

    @InjectMocks
    private ProjectService service;

    @Mock
    private ProjectRepository repository;

    @Mock
    private ModelMapper modelMapper;

    @BeforeEach
    void setUp() throws Exception {
        input = new MockProject();
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAll() {
    }

    @Test
    void findById() {
        Project project = input.mockEntity(1);
        ProjectResponseDTO projectDTO = new ProjectResponseDTO();

        projectDTO.setId(project.getId());
        projectDTO.setName(project.getName());
        projectDTO.setStatus(project.getStatus());
        projectDTO.setCreatedAt(project.getCreatedAt());
        projectDTO.setUpdatedAt(project.getUpdatedAt());

        when(repository.findById(1L)).thenReturn(Optional.of(project));
        when(modelMapper.map(project, ProjectResponseDTO.class)).thenReturn(projectDTO);

        var result = service.findById(1L);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getLinks());

        assertNotNull(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("self")
                        && link.getHref().endsWith("/v1/projects/" + project.getId())
                        && link.getType().equals("GET")
                )
        );

        assertNotNull(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("findAll")
                        && link.getHref().endsWith("/v1/projects/")
                        && link.getType().equals("GET")
                )
        );

        assertNotNull(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("create")
                        && link.getHref().endsWith("/v1/projects/")
                        && link.getType().equals("POST")
                )
        );

        assertNotNull(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("update")
                        && link.getHref().endsWith("/v1/projects/" + project.getId())
                        && link.getType().equals("PUT")
                )
        );

        assertNotNull(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("delete")
                        && link.getHref().endsWith("/v1/projects/" + project.getId())
                        && link.getType().equals("DELETE")
                )
        );

        assertEquals("Project 1", result.getName());
        assertEquals(StatusEnum.NOT_DONE, result.getStatus());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
    }

    @Test
    void create() {
        ProjectResponseDTO dto = input.mockDTO(1);
        Project persisted = new Project();

        persisted.setId(dto.getId());
        persisted.setName(dto.getName());
        persisted.setStatus(dto.getStatus());
        persisted.setCreatedAt(dto.getCreatedAt());
        persisted.setUpdatedAt(dto.getUpdatedAt());

        ProjectCreateDTO dtoCreate = new ProjectCreateDTO(dto.getName(), dto.getStatus());
        Project project = new Project(null, dtoCreate.getName(), dtoCreate.getStatus(), null, null);


        when(modelMapper.map(dtoCreate, Project.class)).thenReturn(project);
    }

    @Test
    void update() {
    }

    @Test
    void deleteById() {
    }
}