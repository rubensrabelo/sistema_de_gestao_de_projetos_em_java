package com.management.project.unittest.mocks;

import com.management.project.controller.CollaboratorController;
import com.management.project.data.dto.collaborator.CollaboratorCreateDTO;
import com.management.project.data.dto.collaborator.CollaboratorResponseDTO;
import com.management.project.data.dto.collaborator.CollaboratorUpdateDTO;
import com.management.project.model.Collaborator;
import com.management.project.model.enums.FunctionEnum;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class MockCollaborator {

    public Collaborator mockEntity(int size) {
        return createMockEntity(size);
    }

    public List<Collaborator> mockListEntity(int size) {
        return createMockListEntity(size);
    }

    public CollaboratorResponseDTO mockDTO(int size) {
        return createMockDTO(size);
    }

    public List<CollaboratorResponseDTO> mockListDTO(int size) {
        return createMockListDTO(size);
    }

    private Collaborator createMockEntity(int size) {
        Collaborator collaborator = new Collaborator();

        collaborator.setId((long) size);
        collaborator.setName("Collaborator " + size);
        collaborator.setEmail("collaborator" + size + "@gmail.com");
        collaborator.setFunction(createFunction(size));

        return collaborator;
    }

    private List<Collaborator> createMockListEntity(int size) {
        List<Collaborator> collaborators = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            Collaborator collaborator = createMockEntity(i);
            collaborators.add(collaborator);
        }

        return collaborators;
    }

    private CollaboratorResponseDTO createMockDTO(int size) {
        CollaboratorResponseDTO collaboratorDTO = new CollaboratorResponseDTO();

        collaboratorDTO.setId((long) size);
        collaboratorDTO.setName("CollaboratorDTO " + size);
        collaboratorDTO.setEmail("collaborator" + size + "@gmail.com");
        collaboratorDTO.setFunction(createFunction(size));

        addHateoasLinks(collaboratorDTO);

        return collaboratorDTO;
    }

    private List<CollaboratorResponseDTO> createMockListDTO(int size) {
        List<CollaboratorResponseDTO> collaboratorDTOs = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            CollaboratorResponseDTO collaboratorDTO = createMockDTO(i);
            collaboratorDTOs.add(collaboratorDTO);
        }

        return collaboratorDTOs;
    }

    private FunctionEnum createFunction(int num) {
        FunctionEnum function;

        if(num % 2 == 0) {
            function = FunctionEnum.DEVELOPER;;
        } else if(num % 3 == 0) {
            function = FunctionEnum.ANALYST;
        } else if(num % 5 == 0) {
            function = FunctionEnum.DESIGNER;
        } else if(num % 7 == 0) {
            function = FunctionEnum.TESTER;
        } else {
            function = FunctionEnum.MANAGER;
        }

        return function;
    }

    private void addHateoasLinks(CollaboratorResponseDTO dto) {
        dto.add(linkTo(methodOn(CollaboratorController.class).findAll(0, 10, "desc"))
                .withRel("findAll").withType("GET"));
        dto.add(linkTo(methodOn(CollaboratorController.class).findById(dto.getId()))
                .withSelfRel().withType("GET"));
        dto.add(linkTo(methodOn(CollaboratorController.class).create(new CollaboratorCreateDTO(dto.getName(), dto.getEmail(), dto.getFunction())))
                .withRel("create").withType("POST"));
        dto.add(linkTo(methodOn(CollaboratorController.class).update(dto.getId(), new CollaboratorUpdateDTO(dto.getName(), dto.getEmail(), dto.getFunction())))
                .withRel("update").withType("PUT"));
        dto.add(linkTo(methodOn(CollaboratorController.class).delete(dto.getId()))
                .withRel("delete").withType("DELETE"));
    }
}
