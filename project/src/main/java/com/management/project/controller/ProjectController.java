package com.management.project.controller;

import com.management.project.controller.docs.ProjectControllerDocs;
import com.management.project.data.dto.project.*;
import com.management.project.service.ProjectService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/projects")
@Tag(name = "Projects", description = "Endpoints for Managing Projects")
public class ProjectController implements ProjectControllerDocs {

    @Autowired
    private ProjectService service;

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    @Override
    public ResponseEntity<PagedModel<EntityModel<ProjectResponseDTO>>> findAll(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction
    ) {
        var sortDirection = direction.equalsIgnoreCase("asc") ? Direction.ASC : Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "name"));
        return ResponseEntity.ok().body(service.findAll(pageable));
    }

    @GetMapping(value = "/count", produces = {MediaType.APPLICATION_JSON_VALUE})
    @Override
    public ResponseEntity<Map<String, Long>> countProjects() {
        long countProducts = service.countProducts();
        Map<String, Long> products = Map.of("products", countProducts);
        return ResponseEntity.ok().body(products);
    }

    @GetMapping(value = "/task-count", produces = {MediaType.APPLICATION_JSON_VALUE})
    @Override
    public ResponseEntity<List<ProjectTaskCountDTO>> countTasksPerProject() {
        List<ProjectTaskCountDTO> result = service.countTasksPerProject();
        if(result.isEmpty())
            return ResponseEntity.noContent().build();
        return ResponseEntity.ok().body(result);
    }

    @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @Override
    public ResponseEntity<ProjectResponseWithTasksDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok().body(service.findById(id));
    }

    @PostMapping(
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE}
    )
    @Override
    public ResponseEntity<ProjectResponseDTO> create(@RequestBody ProjectCreateDTO dto) {
        ProjectResponseDTO response = service.create(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(response.getId()).toUri();
        return ResponseEntity.created(uri).body(response);
    }

    @PutMapping(
            value = "/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE}
    )
    @Override
    public ResponseEntity<ProjectResponseDTO> update(@PathVariable Long id, ProjectUpdateDTO dto) {
        ProjectResponseDTO response = service.update(id, dto);
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping(value = "/{id}")
    @Override
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
