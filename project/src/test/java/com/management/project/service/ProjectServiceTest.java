package com.management.project.service;

import com.management.project.data.dto.project.ProjectCreateDTO;
import com.management.project.data.dto.project.ProjectResponseDTO;
import com.management.project.data.dto.project.ProjectResponseWithTasksDTO;
import com.management.project.data.dto.project.ProjectUpdateDTO;
import com.management.project.mocks.MockProject;
import com.management.project.model.Project;
import com.management.project.model.Task;
import com.management.project.model.enums.StatusEnum;
import com.management.project.repository.ProjectRepository;
import com.management.project.service.exceptions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;


import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Mock
    private PagedResourcesAssembler<ProjectResponseDTO> assembler;

    @BeforeEach
    void setUp() throws Exception {
        input = new MockProject();
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAll() {
        List<Project> entities = input.mockListEntity(5);
        List<ProjectResponseDTO> dtoResponse = input.mockListDTO(5);

        Page<Project> page = new PageImpl<>(entities, PageRequest.of(0, 10), entities.size());

        when(repository.findAll(any(PageRequest.class))).thenReturn(page);
        for (int i = 0; i < entities.size(); i++) {
            when(modelMapper.map(entities.get(i), ProjectResponseDTO.class)).thenReturn(dtoResponse.get(i));
        }

        List<EntityModel<ProjectResponseDTO>> entityModels = dtoResponse.stream()
                .map(dto -> EntityModel.of(dto,
                        Link.of("/v1/projects/" + dto.getId()).withSelfRel(),
                        Link.of("/v1/projects/").withRel("findAll"),
                        Link.of("/v1/projects/").withRel("create"),
                        Link.of("/v1/projects/" + dto.getId()).withRel("update"),
                        Link.of("/v1/projects/" + dto.getId()).withRel("delete")
                ))
                .collect(Collectors.toList());

        PagedModel<EntityModel<ProjectResponseDTO>> pagedModel = PagedModel.of(entityModels,
                new PagedModel.PageMetadata(10, 0, dtoResponse.size()));

        when(assembler.toModel(any(Page.class), any(Link.class))).thenReturn(pagedModel);

        var result = service.findAll(PageRequest.of(0, 10));

        assertNotNull(result);
        assertFalse(result.getContent().isEmpty());
        assertEquals(5, result.getContent().size());

        result.getContent().forEach(entityModel -> {
            assertNotNull(entityModel.getContent());
            ProjectResponseDTO dto = entityModel.getContent();

            assertNotNull(dto.getId());
            assertNotNull(dto.getName());
            assertNotNull(dto.getStatus());
            assertNotNull(dto.getCreatedAt());
            assertNotNull(dto.getUpdatedAt());

            assertTrue(entityModel.getLinks().stream().anyMatch(link -> link.getRel().value().equals("self")
                    && link.getHref().endsWith("/v1/projects/" + dto.getId())));

            assertTrue(entityModel.getLinks().stream().anyMatch(link -> link.getRel().value().equals("findAll")
                    && link.getHref().endsWith("/v1/projects/")));

            assertTrue(entityModel.getLinks().stream().anyMatch(link -> link.getRel().value().equals("create")
                    && link.getHref().endsWith("/v1/projects/")));

            assertTrue(entityModel.getLinks().stream().anyMatch(link -> link.getRel().value().equals("update")
                    && link.getHref().endsWith("/v1/projects/" + dto.getId())));

            assertTrue(entityModel.getLinks().stream().anyMatch(link -> link.getRel().value().equals("delete")
                    && link.getHref().endsWith("/v1/projects/" + dto.getId())));
        });

        verify(repository, times(1)).findAll(any(PageRequest.class));
        verify(modelMapper, times(5)).map(any(Project.class), eq(ProjectResponseDTO.class));
        verify(assembler, times(1)).toModel(any(Page.class), any(Link.class));
    }

    @Test
    void findById() {
        Project entity = input.mockEntity(1);

        Task task = new Task("Task 01", StatusEnum.NOT_DONE, entity);
        task.setId(1L);
        task.setCreatedAt(Instant.now());
        task.setUpdatedAt(Instant.now());

        entity.addTask(task);

        ProjectResponseWithTasksDTO dtoResponse = new ProjectResponseWithTasksDTO();
        dtoResponse.setId(entity.getId());
        dtoResponse.setName(entity.getName());
        dtoResponse.setStatus(entity.getStatus());
        dtoResponse.setCreatedAt(entity.getCreatedAt());
        dtoResponse.setUpdatedAt(entity.getUpdatedAt());

        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(modelMapper.map(entity, ProjectResponseWithTasksDTO.class)).thenReturn(dtoResponse);

        var result = service.findById(1L);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getLinks());

        assertEquals("Project 1", result.getName());
        assertEquals(StatusEnum.NOT_DONE, result.getStatus());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());

        assertLinkExists(result, "self", "/v1/projects/" + dtoResponse.getId(), "GET");
        assertLinkExists(result, "findAll", "/v1/projects/", "GET");
        assertLinkExists(result, "create", "/v1/projects/", "POST");
        assertLinkExists(result, "update", "/v1/projects/" + dtoResponse.getId(), "PUT");
        assertLinkExists(result, "delete", "/v1/projects/" + dtoResponse.getId(), "DELETE");

        verify(repository, times(1)).findById(1L);
        verify(modelMapper, times(1)).map(entity, ProjectResponseWithTasksDTO.class);
    }

    @Test
    void testFindByIdWithIdDoesNotExist() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.findById(1L)
        );

        String expectedMessage = "Project not found";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
        verify(repository, times(1)).findById(1L);
        verifyNoInteractions(modelMapper);
    }


    @Test
    void create() {
        ProjectResponseDTO dtoResponse = input.mockDTO(1);
        Project persisted = input.mockEntity(1);
        ProjectCreateDTO dtoCreate = new ProjectCreateDTO(dtoResponse.getName(), dtoResponse.getStatus());

        when(modelMapper.map(dtoCreate, Project.class)).thenReturn(persisted);
        when(repository.save(persisted)).thenReturn(persisted);
        when(modelMapper.map(persisted, ProjectResponseDTO.class)).thenReturn(dtoResponse);

        var result = service.create(dtoCreate);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getLinks());

        assertEquals("ProjectDTO 1", result.getName());
        assertEquals(StatusEnum.NOT_DONE, result.getStatus());

        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());

        assertLinkExists(result, "self", "/v1/projects/" + dtoResponse.getId(), "GET");
        assertLinkExists(result, "findAll", "/v1/projects/", "GET");
        assertLinkExists(result, "create", "/v1/projects/", "POST");
        assertLinkExists(result, "update", "/v1/projects/" + dtoResponse.getId(), "PUT");
        assertLinkExists(result, "delete", "/v1/projects/" + dtoResponse.getId(), "DELETE");

        verify(modelMapper, times(1)).map(dtoCreate, Project.class);
        verify(repository, times(1)).save(persisted);
        verify(modelMapper, times(1)).map(persisted, ProjectResponseDTO.class);
    }

    @Test
    void testCreateWithNullProject() {
        Exception exception = assertThrows(
                RequiredObjectIsNullException.class,
                () -> service.create(null)
        );

        String expectedMessage = "Its is not allowed to persist a null object";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
        verifyNoMoreInteractions(repository, modelMapper);
    }

    @Test
    void testCreateWithNullName() {
        ProjectCreateDTO dtoCreate = new ProjectCreateDTO(null, StatusEnum.NOT_DONE);
        Exception exception = assertThrows(
                EmptyNameException.class,
                () -> service.create(dtoCreate)
        );

        String expectedMessage = "The name cannot be null or blank.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
        verifyNoMoreInteractions(repository, modelMapper);
    }

    @Test
    void testCreateWithErrorSizeName() {
        ProjectCreateDTO dtoCreate = new ProjectCreateDTO("Pr", StatusEnum.NOT_DONE);
        Exception exception = assertThrows(
                InvalidNameSizeException.class,
                () -> service.create(dtoCreate)
        );

        String expectedMessage = "The name field must be between 3 and 100 characters.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
        verifyNoMoreInteractions(repository, modelMapper);
    }

    @Test
    void update() {
        ProjectResponseDTO dtoResponse = input.mockDTO(1);
        Project project = input.mockEntity(1);
        ProjectUpdateDTO dtoUpdate = new ProjectUpdateDTO(dtoResponse.getName(), dtoResponse.getStatus());

        when(repository.findById(1L)).thenReturn(Optional.of(project));
        when(repository.save(project)).thenReturn(project);
        when(modelMapper.map(project, ProjectResponseDTO.class)).thenReturn(dtoResponse);

        var result = service.update(1L, dtoUpdate);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getLinks());

        assertEquals("ProjectDTO 1", result.getName());
        assertEquals(StatusEnum.NOT_DONE, result.getStatus());

        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());

        assertLinkExists(result, "self", "/v1/projects/" + dtoResponse.getId(), "GET");
        assertLinkExists(result, "findAll", "/v1/projects/", "GET");
        assertLinkExists(result, "create", "/v1/projects/", "POST");
        assertLinkExists(result, "update", "/v1/projects/" + dtoResponse.getId(), "PUT");
        assertLinkExists(result, "delete", "/v1/projects/" + dtoResponse.getId(), "DELETE");

        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).save(project);
        verify(modelMapper, times(1)).map(project, ProjectResponseDTO.class);
    }

    @Test
    void testUpdateWithIdDoesNotExist() {
        ProjectUpdateDTO dtoUpdate = new ProjectUpdateDTO("Updated Project", StatusEnum.NOT_DONE);

        when(repository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.update(1L, dtoUpdate)
        );

        String expectedMessage = "Project not found";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
        verify(repository, times(1)).findById(1L);
        verifyNoMoreInteractions(repository, modelMapper);
    }

    @Test
    void testUpdateWithNullProject() {
        Exception exception = assertThrows(
                RequiredObjectIsNullException.class,
                () -> service.update(1L, null)
        );

        String expectedMessage = "Its is not allowed to persist a null object";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
        verifyNoMoreInteractions(repository, modelMapper);
    }

    @Test
    void testUpdateWithErrorSizeName() {
        ProjectUpdateDTO dtoUpdate = new ProjectUpdateDTO("Pr", StatusEnum.NOT_DONE);
        Project project = new Project("Valid Name", StatusEnum.NOT_DONE);
        project.setId(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(project));
        Exception exception = assertThrows(
                InvalidNameSizeException.class,
                () -> service.update(1L, dtoUpdate)
        );

        String expectedMessage = "The name field must be between 3 and 100 characters.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
        verifyNoMoreInteractions(repository, modelMapper);
    }

    @Test
    void deleteById() {

        doNothing().when(repository).deleteById(1L);

        assertDoesNotThrow(() -> service.deleteById(1L));
        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteWithIdDoesNotExist() {
        doThrow(new EmptyResultDataAccessException(1)).when(repository).deleteById(1L);

        Exception exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.deleteById(1L)
        );

        String expectedMessage = "Project not found";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteWithDatabaseException() {
        doThrow(new DataIntegrityViolationException("Database error")).when(repository).deleteById(1L);

        Exception exception = assertThrows(
                DatabaseException.class,
                () -> service.deleteById(1L)
        );

        String expectedMessage = "Database error";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
        verify(repository, times(1)).deleteById(1L);
    }

    private void assertLinkExists(ProjectResponseDTO dto, String rel, String href, String type) {
        dto.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals(rel)
                        && link.getHref().endsWith(href)
                        && link.getType().equals(type)
                );
    }

    private void assertLinkExists(ProjectResponseWithTasksDTO dto, String rel, String href, String type) {
        dto.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals(rel)
                        && link.getHref().endsWith(href)
                        && link.getType().equals(type)
                );
    }
}