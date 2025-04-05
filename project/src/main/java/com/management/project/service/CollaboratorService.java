package com.management.project.service;

import com.management.project.controller.CollaboratorController;
import com.management.project.data.dto.collaborator.CollaboratorCreateDTO;
import com.management.project.data.dto.collaborator.CollaboratorResponseDTO;
import com.management.project.data.dto.collaborator.CollaboratorTaskCount;
import com.management.project.data.dto.collaborator.CollaboratorUpdateDTO;
import com.management.project.model.Collaborator;
import com.management.project.repository.CollaboratorRepository;
import com.management.project.service.exceptions.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@Service
public class CollaboratorService {

    @Autowired
    private CollaboratorRepository repository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    PagedResourcesAssembler<CollaboratorResponseDTO> assembler;

    public PagedModel<EntityModel<CollaboratorResponseDTO>> findAll(Pageable pageable) {
        var dtoResponse = repository.findAll(pageable)
                .map(coll ->{
                    var dto = modelMapper.map(coll, CollaboratorResponseDTO.class);
                    addHateoasLinks(dto);
                    return dto;
                });
        Link findAllLinks = WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(CollaboratorController.class)
                        .findAll(
                                pageable.getPageNumber(),
                                pageable.getPageSize(),
                                String.valueOf(pageable.getSort())
                        )
        ).withSelfRel();
        return assembler.toModel(dtoResponse, findAllLinks);
    }

    public CollaboratorResponseDTO findById(Long id) {
        Collaborator entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Collaborator not found"));
        CollaboratorResponseDTO dtoResponse = modelMapper.map(entity, CollaboratorResponseDTO.class);
        addHateoasLinks(dtoResponse);
        return dtoResponse;
    }

    public long countCollaborators() {
        return repository.count();
    }

    public List<CollaboratorTaskCount> countTasksPerCollaborator() {
        return repository.countTasksPerCollaborator();
    }

    public CollaboratorResponseDTO create(CollaboratorCreateDTO dto) {
        if(dto == null)
            throw new RequiredObjectIsNullException();

        if(dto.getName() == null || dto.getName().isEmpty())
            throw new EmptyNameException("The name cannot be null or blank.");

        if(dto.getName().length() < 3 || dto.getName().length() > 100)
            throw new InvalidNameSizeException("The name field must be between 3 and 100 characters.");

        if(!isValidEmail(dto.getEmail()))
            throw new InvalidEmailException("The email provided is invalid.");

        if(repository.existsByEmail(dto.getEmail()))
            throw new EmailAlreadyExistsException("The email " + dto.getEmail() + " is already in use.");

        Collaborator entity = modelMapper.map(dto, Collaborator.class);
        repository.save(entity);
        CollaboratorResponseDTO dtoResponse = modelMapper.map(entity, CollaboratorResponseDTO.class);
        addHateoasLinks(dtoResponse);
        return dtoResponse;
    }

    public CollaboratorResponseDTO update(Long id, CollaboratorUpdateDTO updatedData) {
        if(updatedData == null)
            throw new RequiredObjectIsNullException();

        Collaborator entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Collaborator not found"));

        updateData(entity, updatedData);
        repository.save(entity);
        CollaboratorResponseDTO dtoResponse = modelMapper.map(entity, CollaboratorResponseDTO.class);
        addHateoasLinks(dtoResponse);
        return dtoResponse;
    }

    public void deleteById(Long id) {
        try {
            repository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException("Collaborator not found");
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    private void updateData(Collaborator entity, CollaboratorUpdateDTO updatedData) {
        if(updatedData.getName() != null) {
            if(updatedData.getName().length() < 3 || updatedData.getName().length() > 100)
                throw new InvalidNameSizeException("The name field must be between 3 and 100 characters.");
            entity.setName(updatedData.getName());
        }

        if(updatedData.getEmail() != null) {
            if(!isValidEmail(updatedData.getEmail()))
                throw new InvalidEmailException("The email provided is invalid.");
            entity.setEmail(updatedData.getEmail());
        }

        if(updatedData.getFunction() != null)
            entity.setFunction(updatedData.getFunction());
    }

    private boolean isValidEmail(String email) {
        final String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private void addHateoasLinks(CollaboratorResponseDTO dto) {
        dto.add(linkTo(methodOn(CollaboratorController.class).findAll(0, 10, "desc")).withRel("findAll").withType("GET"));
        dto.add(linkTo(methodOn(CollaboratorController.class).findById(dto.getId())).withSelfRel().withType("GET"));

        CollaboratorCreateDTO dtoCreated = new CollaboratorCreateDTO(dto.getName(), dto.getEmail(), dto.getFunction());
        dto.add(linkTo(methodOn(CollaboratorController.class).create(dtoCreated)).withRel("create").withType("POST"));

        CollaboratorUpdateDTO dtoUpdated = new CollaboratorUpdateDTO(dto.getName(), dto.getEmail(), dto.getFunction());
        dto.add(linkTo(methodOn(CollaboratorController.class).update(dto.getId(), dtoUpdated)).withRel("update").withType("PUT"));

        dto.add(linkTo(methodOn(CollaboratorController.class).delete(dto.getId())).withRel("delete").withType("DELETE"));
    }
}
