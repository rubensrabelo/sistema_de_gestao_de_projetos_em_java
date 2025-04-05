package com.management.project.controller;

import com.management.project.controller.docs.TaskControllerDocs;
import com.management.project.data.dto.collaborator_task.CollaboratorTaskDTO;
import com.management.project.data.dto.task.TaskCollaboratorCountDTO;
import com.management.project.data.dto.task.TaskCreateDTO;
import com.management.project.data.dto.task.TaskResponseDTO;
import com.management.project.data.dto.task.TaskUpdateDTO;
import com.management.project.service.TaskService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/tasks")
@Tag(name = "Tasks", description = "Endpoints for Managing Tasks")
public class TaskController implements TaskControllerDocs {

    @Autowired
    private TaskService service;

    @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @Override
    public ResponseEntity<TaskResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok().body(service.findById(id));
    }

    @GetMapping(value = "/projects/{projectId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @Override
    public ResponseEntity<List<TaskCollaboratorCountDTO>> countCollaboratorsPerTaskByProjectId(
            @PathVariable("projectId") Long projectId
    ) {
        List<TaskCollaboratorCountDTO> result = service.countCollaboratorsPerTaskByProjectId(projectId);
        if(result.isEmpty())
            return ResponseEntity.noContent().build();
        return ResponseEntity.ok().body(result);
    }


    @PostMapping(
            path = "/assign-collaborator",
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE}
    )
    @Override
    public ResponseEntity<Map<String, String>> assignCollaborator(@RequestBody CollaboratorTaskDTO dto) {
        Map<String, String> response = service.assignCollaboratorToTask(dto);
        return ResponseEntity.ok(response);
    }

    @PostMapping(
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE}
    )
    @Override
    public ResponseEntity<TaskResponseDTO> create(@RequestBody TaskCreateDTO dto) {
        TaskResponseDTO response = service.create(dto);
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
    public ResponseEntity<TaskResponseDTO> update(@PathVariable Long id, TaskUpdateDTO dto) {
        TaskResponseDTO response = service.update(id, dto);
        return ResponseEntity.ok().body(response);
    }
}
