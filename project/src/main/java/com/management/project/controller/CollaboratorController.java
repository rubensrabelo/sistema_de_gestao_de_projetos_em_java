package com.management.project.controller;

import com.management.project.controller.docs.CollaboratorControllerDocs;
import com.management.project.data.dto.collaborator.CollaboratorCreateDTO;
import com.management.project.data.dto.collaborator.CollaboratorResponseDTO;
import com.management.project.data.dto.collaborator.CollaboratorTaskCount;
import com.management.project.data.dto.collaborator.CollaboratorUpdateDTO;
import com.management.project.service.CollaboratorService;
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
@RequestMapping("/v1/collaborators")
@Tag(name = "Collaborators", description = "Endpoints for Managing Collaborators")
public class CollaboratorController implements CollaboratorControllerDocs {

    @Autowired
    private CollaboratorService service;

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    @Override
    public ResponseEntity<PagedModel<EntityModel<CollaboratorResponseDTO>>> findAll(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction
    ) {
        var sortDirection = direction.equalsIgnoreCase("asc") ? Direction.ASC : Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "name"));
        return ResponseEntity.ok().body(service.findAll(pageable));
    }

    @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @Override
    public ResponseEntity<CollaboratorResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok().body(service.findById(id));
    }

    @GetMapping(value = "/count", produces = {MediaType.APPLICATION_JSON_VALUE})
    @Override
    public ResponseEntity<Map<String, Long>> countProjects() {
        Long result = service.countCollaborators();
        Map<String, Long> collaborators = Map.of("collaborators", result);
        return ResponseEntity.ok().body(collaborators);
    }

    @GetMapping(value = "/task-count", produces = {MediaType.APPLICATION_JSON_VALUE})
    @Override
    public ResponseEntity<List<CollaboratorTaskCount>> countTasksPerCollaborator() {
        List<CollaboratorTaskCount> result = service.countTasksPerCollaborator();
        if(result.isEmpty())
            return ResponseEntity.noContent().build();
        return ResponseEntity.ok().body(result);
    }

    @PostMapping(
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE}
    )
    @Override
    public ResponseEntity<CollaboratorResponseDTO> create(@RequestBody CollaboratorCreateDTO dto) {
        CollaboratorResponseDTO response = service.create(dto);
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
    public ResponseEntity<CollaboratorResponseDTO> update(@PathVariable Long id, CollaboratorUpdateDTO dto) {
        CollaboratorResponseDTO response = service.update(id, dto);
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping(value = "/{id}")
    @Override
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
