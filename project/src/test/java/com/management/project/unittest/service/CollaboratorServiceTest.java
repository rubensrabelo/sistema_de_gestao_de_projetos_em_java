package com.management.project.unittest.service;

import com.management.project.data.dto.collaborator.CollaboratorResponseDTO;
import com.management.project.model.Collaborator;
import com.management.project.model.enums.FunctionEnum;
import com.management.project.repository.CollaboratorRepository;
import com.management.project.service.CollaboratorService;
import com.management.project.unittest.mocks.MockCollaborator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.data.web.PagedResourcesAssembler;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
    void create() {
    }

    @Test
    void update() {
    }

    @Test
    void deleteById() {

        doNothing().when(repository).deleteById(1L);

        assertDoesNotThrow(() -> service.deleteById(1L));
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