package com.management.project.service;

import com.management.project.data.dto.project.ProjectCreateDTO;
import com.management.project.data.dto.project.ProjectResponseDTO;
import com.management.project.data.dto.project.ProjectUpdateDTO;
import com.management.project.model.Project;
import com.management.project.repository.ProjectRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository repository;

    @Autowired
    private ModelMapper modelMapper;

    public Page<ProjectResponseDTO> findAll(Pageable pageable) {
        return repository.findAll(pageable)
                .map(p -> modelMapper.map(p, ProjectResponseDTO.class));
    }

    public ProjectResponseDTO findById(Long id) {
        Project entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        return modelMapper.map(entity, ProjectResponseDTO.class);
    }

    public ProjectResponseDTO create(ProjectCreateDTO dto) {
        Project entity = modelMapper.map(dto, Project.class);
        repository.save(entity);
        return modelMapper.map(entity, ProjectResponseDTO.class);
    }

    public ProjectResponseDTO update(Long id, ProjectUpdateDTO updatedData) {
        Project entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        Project dataProject = modelMapper.map(updatedData, Project.class);
        updateData(entity, dataProject);
        repository.save(entity);
        return modelMapper.map(entity, ProjectResponseDTO.class);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    private void updateData(Project entity, Project dataProject) {
        entity.setName(dataProject.getName());
        entity.setStatus(dataProject.getStatus());
    }
}
