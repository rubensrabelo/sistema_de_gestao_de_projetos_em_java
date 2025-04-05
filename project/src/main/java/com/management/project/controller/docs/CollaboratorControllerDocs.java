package com.management.project.controller.docs;

import com.management.project.data.dto.collaborator.CollaboratorCreateDTO;
import com.management.project.data.dto.collaborator.CollaboratorResponseDTO;
import com.management.project.data.dto.collaborator.CollaboratorTaskCount;
import com.management.project.data.dto.collaborator.CollaboratorUpdateDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

public interface CollaboratorControllerDocs {

    @Operation(summary = "Find All Collaborator",
            description = "Finds All Collaborators",
            tags = {"Collaborators"},
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            array = @ArraySchema(schema = @Schema(implementation = CollaboratorResponseDTO.class))
                                    )
                            }),
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    ResponseEntity<PagedModel<EntityModel<CollaboratorResponseDTO>>> findAll(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction
    );

    @Operation(summary = "Finds a Collaborator",
            description = "Find a specific collaborator by your ID",
            tags = {"Collaborators"},
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = @Content(schema = @Schema(implementation = CollaboratorResponseDTO.class))
                    ),
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    ResponseEntity<CollaboratorResponseDTO> findById(@PathVariable("id") Long id);

    @Operation(summary = "Counts all Collaborators",
            description = "Returns the total number of collaborators",
            tags = {"Collaborators"},
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = @Content(schema = @Schema(implementation = Object.class))
                    ),
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    ResponseEntity<Map<String, Long>> countProjects();

    @Operation(summary = "Counts tasks per collaborator",
            description = "Returns the number of tasks assigned to each collaborator, including those with zero tasks",
            tags = {"Collaborators"},
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = CollaboratorTaskCount.class)))
                    ),
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    ResponseEntity<List<CollaboratorTaskCount>> countTasksPerCollaborator();


    @Operation(summary = "Adds a new Collaborator",
            description = "Adds a new Collaborator by passing in a JSON representation of the project.",
            tags = {"Collaborators"},
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = @Content(schema = @Schema(implementation = CollaboratorResponseDTO.class))
                    ),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    ResponseEntity<CollaboratorResponseDTO> create(@RequestBody CollaboratorCreateDTO dtoCreate);

    @Operation(summary = "Updates a collaborator's information",
            description = "Updates a collaborators's information by passing in a JSON representation of the updated collaborators.",
            tags = {"Collaborators"},
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = @Content(schema = @Schema(implementation = CollaboratorResponseDTO.class))
                    ),
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    ResponseEntity<CollaboratorResponseDTO> update(@PathVariable("id") Long id, @RequestBody CollaboratorUpdateDTO dtoUpdate);

    @Operation(summary = "Deletes a Collaborator",
            description = "Deletes a specific collaborator by their ID",
            tags = {"Collaborators"},
            responses = {
                    @ApiResponse(
                            description = "No Content",
                            responseCode = "204", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    ResponseEntity<Void> delete(@PathVariable("id") Long id);
}
