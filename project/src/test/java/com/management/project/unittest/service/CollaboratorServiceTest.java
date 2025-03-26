package com.management.project.unittest.service;

import com.management.project.data.dto.collaborator.CollaboratorCreateDTO;
import com.management.project.data.dto.collaborator.CollaboratorResponseDTO;
import com.management.project.data.dto.collaborator.CollaboratorUpdateDTO;
import com.management.project.model.Collaborator;
import com.management.project.model.enums.FunctionEnum;
import com.management.project.repository.CollaboratorRepository;
import com.management.project.service.CollaboratorService;
import com.management.project.service.exceptions.*;
import com.management.project.unittest.mocks.MockCollaborator;
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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class CollaboratorServiceTest {

    MockCollaborator input;

    @InjectMocks
    private CollaboratorService service;

    @Mock
    private CollaboratorRepository repository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PagedResourcesAssembler<CollaboratorResponseDTO> assembler;


    @BeforeEach
    void setUp() throws Exception {
        input = new MockCollaborator();
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAll() {
        List<Collaborator> entities = input.mockListEntity(5);
        List<CollaboratorResponseDTO> dtoResponse = input.mockListDTO(5);

        Page<Collaborator> page = new PageImpl<>(entities, PageRequest.of(0, 10), entities.size());

        when(repository.findAll(any(PageRequest.class))).thenReturn(page);
        for (int i = 0; i < entities.size(); i++) {
            when(modelMapper.map(entities.get(i), CollaboratorResponseDTO.class)).thenReturn(dtoResponse.get(i));
        }

        List<EntityModel<CollaboratorResponseDTO>> entityModels = dtoResponse.stream()
                .map(dto -> EntityModel.of(dto,
                        Link.of("/v1/collaborators/" + dto.getId()).withSelfRel(),
                        Link.of("/v1/collaborators/").withRel("findAll"),
                        Link.of("/v1/collaborators/").withRel("create"),
                        Link.of("/v1/collaborators/" + dto.getId()).withRel("update"),
                        Link.of("/v1/collaborators/" + dto.getId()).withRel("delete")
                ))
                .collect(Collectors.toList());

        PagedModel<EntityModel<CollaboratorResponseDTO>> pagedModel = PagedModel.of(entityModels,
                new PagedModel.PageMetadata(10, 0, dtoResponse.size()));

        when(assembler.toModel(any(Page.class), any(Link.class))).thenReturn(pagedModel);

        var result = service.findAll(PageRequest.of(0, 10));

        assertNotNull(result);
        assertFalse(result.getContent().isEmpty());
        assertEquals(5, result.getContent().size());

        result.getContent().forEach(entityModel -> {
            assertNotNull(entityModel.getContent());
            CollaboratorResponseDTO dto = entityModel.getContent();

            assertNotNull(dto.getId());
            assertNotNull(dto.getName());
            assertNotNull(dto.getFunction());

            assertTrue(entityModel.getLinks().stream().anyMatch(link -> link.getRel().value().equals("self")
                    && link.getHref().endsWith("/v1/collaborators/" + dto.getId())));

            assertTrue(entityModel.getLinks().stream().anyMatch(link -> link.getRel().value().equals("findAll")
                    && link.getHref().endsWith("/v1/collaborators/")));

            assertTrue(entityModel.getLinks().stream().anyMatch(link -> link.getRel().value().equals("create")
                    && link.getHref().endsWith("/v1/collaborators/")));

            assertTrue(entityModel.getLinks().stream().anyMatch(link -> link.getRel().value().equals("update")
                    && link.getHref().endsWith("/v1/collaborators/" + dto.getId())));

            assertTrue(entityModel.getLinks().stream().anyMatch(link -> link.getRel().value().equals("delete")
                    && link.getHref().endsWith("/v1/collaborators/" + dto.getId())));
        });

        verify(repository, times(1)).findAll(any(PageRequest.class));
        verify(modelMapper, times(5)).map(any(Collaborator.class), eq(CollaboratorResponseDTO.class));
        verify(assembler, times(1)).toModel(any(Page.class), any(Link.class));
    }

    @Test
    void findById() {
        Collaborator entity = input.mockEntity(1);

        CollaboratorResponseDTO dtoResponse = input.mockDTO(1);

        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(modelMapper.map(entity, CollaboratorResponseDTO.class)).thenReturn(dtoResponse);

        var result = service.findById((1L));

        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getLinks());

        assertEquals("CollaboratorDTO 1", result.getName());
        assertNotNull(result.getEmail());
        assertEquals(FunctionEnum.MANAGER, result.getFunction());

        assertLinkExists(result, "self", "/v1/collaborators/" + dtoResponse.getId(), "GET");
        assertLinkExists(result, "findAll", "/v1/collaborators?page=0&size=10&direction=desc", "GET");
        assertLinkExists(result, "create", "/v1/collaborators", "POST");
        assertLinkExists(result, "update", "/v1/collaborators/" + dtoResponse.getId(), "PUT");
        assertLinkExists(result, "delete", "/v1/collaborators/" + dtoResponse.getId(), "DELETE");

        verify(repository, times(1)).findById(1L);
        verify(modelMapper, times(1)).map(entity, CollaboratorResponseDTO.class);
    }

    @Test
    void testFindByIdWithIdDoesNotExist() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.findById(1L)
        );

        String expectedMessage = "Collaborator not found";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
        verify(repository, times(1)).findById(1L);
        verifyNoInteractions(modelMapper);
    }

    @Test
    void create() {
        CollaboratorResponseDTO dtoResponse = input.mockDTO(1);
        Collaborator persisted = input.mockEntity(1);
        CollaboratorCreateDTO dtoCreate = new CollaboratorCreateDTO(
                dtoResponse.getName(),
                dtoResponse.getEmail(),
                dtoResponse.getFunction()
        );

        when(modelMapper.map(dtoCreate, Collaborator.class)).thenReturn(persisted);
        when(repository.save(persisted)).thenReturn(persisted);
        when(modelMapper.map(persisted, CollaboratorResponseDTO.class)).thenReturn(dtoResponse);

        var result = service.create(dtoCreate);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getLinks());

        assertEquals("CollaboratorDTO 1", result.getName());
        assertEquals(FunctionEnum.MANAGER, result.getFunction());

        assertLinkExists(result, "self", "/v1/collaborators/" + dtoResponse.getId(), "GET");
        assertLinkExists(result, "findAll", "/v1/collaborators?page=0&size=10&direction=desc", "GET");
        assertLinkExists(result, "create", "/v1/collaborators", "POST");
        assertLinkExists(result, "update", "/v1/collaborators/" + dtoResponse.getId(), "PUT");
        assertLinkExists(result, "delete", "/v1/collaborators/" + dtoResponse.getId(), "DELETE");

        verify(modelMapper, times(1)).map(dtoCreate, Collaborator.class);
        verify(repository, times(1)).save(persisted);
        verify(modelMapper, times(1)).map(persisted, CollaboratorResponseDTO.class);
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
        CollaboratorCreateDTO dtoCreate = new CollaboratorCreateDTO(
                null,
                "test@test.com",
                FunctionEnum.MANAGER
        );
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
        CollaboratorCreateDTO dtoCreate = new CollaboratorCreateDTO(
                "CB",
                "test@test.com",
                FunctionEnum.MANAGER
        );
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
    void testCreateWhenEmailIsInvalid() {
        CollaboratorCreateDTO dtoCreate = new CollaboratorCreateDTO(
                "John Doe",
                "invalid-email",
                FunctionEnum.DEVELOPER
        );

        InvalidEmailException exception = assertThrows(
                InvalidEmailException.class,
                () -> service.create(dtoCreate)
        );

        assertEquals("The email provided is invalid.", exception.getMessage());
    }

    @Test
    void testCreateWhenEmailAlreadyExists() {
        CollaboratorCreateDTO dtoCreate = new CollaboratorCreateDTO(
                "John Doe",
                "existing@example.com",
                FunctionEnum.DEVELOPER
        );

        when(repository.existsByEmail(dtoCreate.getEmail())).thenReturn(true);

        EmailAlreadyExistsException exception = assertThrows(
                EmailAlreadyExistsException.class,
                () -> service.create(dtoCreate)
        );

        assertEquals("The email existing@example.com is already in use.", exception.getMessage());
    }

    @Test
    void update() {
        CollaboratorResponseDTO dtoResponse = input.mockDTO(1);
        Collaborator entity = input.mockEntity(1);
        CollaboratorUpdateDTO dtoUpdate = new CollaboratorUpdateDTO(
                dtoResponse.getName(),
                dtoResponse.getEmail(),
                dtoResponse.getFunction()
        );

        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(repository.save(entity)).thenReturn(entity);
        when(modelMapper.map(entity, CollaboratorResponseDTO.class)).thenReturn(dtoResponse);

        var result = service.update(1L, dtoUpdate);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getLinks());

        assertEquals("CollaboratorDTO 1", result.getName());
        assertEquals(FunctionEnum.MANAGER, result.getFunction());


        assertLinkExists(result, "self", "/v1/collaborators/" + dtoResponse.getId(), "GET");
        assertLinkExists(result, "findAll", "/v1/collaborators?page=0&size=10&direction=desc", "GET");
        assertLinkExists(result, "create", "/v1/collaborators", "POST");
        assertLinkExists(result, "update", "/v1/collaborators/" + dtoResponse.getId(), "PUT");
        assertLinkExists(result, "delete", "/v1/collaborators/" + dtoResponse.getId(), "DELETE");

        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).save(entity);
        verify(modelMapper, times(1)).map(entity, CollaboratorResponseDTO.class);
    }

    @Test
    void testUpdateWithIdDoesNotExist() {
        CollaboratorUpdateDTO dtoUpdate = new CollaboratorUpdateDTO(
                "Updated Collaborator",
                "test@test.com",
                FunctionEnum.MANAGER);

        when(repository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.update(1L, dtoUpdate)
        );

        String expectedMessage = "Collaborator not found";
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
        CollaboratorUpdateDTO dtoUpdate = new CollaboratorUpdateDTO(
                "CB",
                "test@test.com",
                FunctionEnum.MANAGER
        );
        Collaborator entity = new Collaborator("CB", "test@test.com", FunctionEnum.MANAGER);
        entity.setId(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        Exception exception = assertThrows(
                InvalidNameSizeException.class,
                () -> service.update(1L, dtoUpdate)
        );

        String expectedMessage = "The name field must be between 3 and 100 characters.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testUpdateWhenEmailIsInvalid() {
        CollaboratorUpdateDTO dtoUpdate = new CollaboratorUpdateDTO(
                "John Doe",
                "invalid-email",
                FunctionEnum.DEVELOPER
        );
        Collaborator existingEntity = new Collaborator();

        when(repository.findById(1L)).thenReturn(Optional.of(existingEntity));

        InvalidEmailException exception = assertThrows(
                InvalidEmailException.class,
                () -> service.update(1L, dtoUpdate)
        );

        assertEquals("The email provided is invalid.", exception.getMessage());
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

        String expectedMessage = "Collaborator not found";
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

    private void assertLinkExists(CollaboratorResponseDTO dto, String rel, String href, String type) {
        boolean linkExists = dto.getLinks().stream()
                .anyMatch(link ->
                        link.getRel().value().equals(rel) &&
                                link.getHref().endsWith(href) &&
                                link.getType().equals(type)
                );

        assertTrue(linkExists, "O link '" + rel + "' com href '" + href + "' e type '" + type + "' não foi encontrado.");
    }
}