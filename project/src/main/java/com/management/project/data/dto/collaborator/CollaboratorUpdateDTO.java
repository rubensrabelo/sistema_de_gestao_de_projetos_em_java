package com.management.project.data.dto.collaborator;

import com.management.project.model.enums.FunctionEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CollaboratorUpdateDTO {

    @Size(min = 3, max = 100)
    private String name;

    @Email
    @Size(max = 150)
    private String email;
    private FunctionEnum function;

    public CollaboratorUpdateDTO() {}

    public CollaboratorUpdateDTO(String name, String email, FunctionEnum function) {
        this.name = name;
        this.email = email;
        this.function = function;
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
}
