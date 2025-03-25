package com.management.project.model;

import com.management.project.model.enums.FunctionEnum;
import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "tb_collaborators")
public class Collaborator implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @Column(unique = true, nullable = false)
    private String email;
    private FunctionEnum function;

    @ManyToMany(mappedBy = "collaborators")
    private List<Task> tasks = new ArrayList<>();

    public Collaborator() {}

    public Collaborator(String name, String email, FunctionEnum function) {
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

    public List<Task> getTasks() {
        return tasks;
    }

    public void addTasks(Task task) {
        this.tasks.add(task);
        task.getCollaborators().add(this);
    }

    public void removeTasks(Task task) {
        this.tasks.remove(task);
        task.getCollaborators().remove(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Collaborator that = (Collaborator) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(email, that.email) && function == that.function;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, email, function);
    }
}
