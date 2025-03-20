package com.management.project.data.dto.collaborator;

import com.management.project.model.enums.FunctionEnum;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class CollaboratorResponseDTO extends RepresentationModel<CollaboratorResponseDTO> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String email;
    private FunctionEnum function;

    public CollaboratorResponseDTO() {}

    public CollaboratorResponseDTO(Long id, String name, String email, FunctionEnum function) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.function = function;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public FunctionEnum getFunction() {
        return function;
    }

    public void setFunction(FunctionEnum function) {
        this.function = function;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        CollaboratorResponseDTO that = (CollaboratorResponseDTO) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(email, that.email) && function == that.function;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, name, email, function);
    }
}
