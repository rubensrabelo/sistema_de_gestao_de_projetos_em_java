package com.management.project.service;

import com.management.project.controller.ProjectController;
import com.management.project.data.dto.project.ProjectCreateDTO;
import com.management.project.data.dto.project.ProjectResponseDTO;
import com.management.project.data.dto.project.ProjectUpdateDTO;
import com.management.project.model.Project;
import com.management.project.repository.ProjectRepository;
import com.management.project.service.exceptions.DatabaseException;
import com.management.project.service.exceptions.ResourceNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository repository;

    @Autowired
    private ModelMapper modelMapper;

    public Page<ProjectResponseDTO> findAll(Pageable pageable) {
        var projects = repository.findAll(pageable)
                .map(p -> modelMapper.map(p, ProjectResponseDTO.class));
        projects.forEach(this::addHateoasLinks);
        return projects;
    }

    public ProjectResponseDTO findById(Long id) {
        Project entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        ProjectResponseDTO dtoResponse = modelMapper.map(entity, ProjectResponseDTO.class);
        addHateoasLinks(dtoResponse);
        return dtoResponse;
    }

    public ProjectResponseDTO create(ProjectCreateDTO dto) {
        Project entity = modelMapper.map(dto, Project.class);
        repository.save(entity);
        ProjectResponseDTO dtoResponse = modelMapper.map(entity, ProjectResponseDTO.class);
        addHateoasLinks(dtoResponse);
        return dtoResponse;
    }

    public ProjectResponseDTO update(Long id, ProjectUpdateDTO updatedData) {
        Project entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        Project dataProject = modelMapper.map(updatedData, Project.class);
        updateData(entity, dataProject);
        repository.save(entity);
        ProjectResponseDTO dtoResponse = modelMapper.map(entity, ProjectResponseDTO.class);
        addHateoasLinks(dtoResponse);
        return dtoResponse;
    }

    public void deleteById(Long id) {
        try {
            repository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException("Project not found");
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    private void updateData(Project entity, Project dataProject) {
        entity.setName(dataProject.getName());
        entity.setStatus(dataProject.getStatus());
    }

    private void addHateoasLinks(ProjectResponseDTO dto) {
        Pageable defaultPageable = PageRequest.of(0, 10);
        dto.add(linkTo(methodOn(ProjectController.class).findAll(defaultPageable)).withRel("findAll").withType("GET"));
        dto.add(linkTo(methodOn(ProjectController.class).findById(dto.getId())).withSelfRel().withType("GET"));

        ProjectCreateDTO dtoCreated = new ProjectCreateDTO(dto.getName(), dto.getStatus());
        dto.add(linkTo(methodOn(ProjectController.class).create(dtoCreated)).withRel("create").withType("POST"));

        ProjectUpdateDTO dtoUpdated = new ProjectUpdateDTO(dto.getName(), dto.getStatus());
        dto.add(linkTo(methodOn(ProjectController.class).update(dto.getId(), dtoUpdated)).withRel("update").withType("PUT"));

        dto.add(linkTo(methodOn(ProjectController.class).delete(dto.getId())).withRel("delete").withType("DELETE"));
    }
}
