package com.management.project.repository;

import com.management.project.data.dto.task.TaskCollaboratorCountDTO;
import com.management.project.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    Page<Task> findAllByProjectId(Long id, Pageable page);

    @Query("""
        SELECT
            new com.management.project.data.dto.task.TaskCollaboratorCountDTO(t.id, t.name, COUNT(c))
        FROM Task t
        LEFT JOIN t.collaborators c
        WHERE t.project.id  = :projectId
        GROUP BY t.id, t.name
        ORDER BY COUNT(c) DESC
    """)
    List<TaskCollaboratorCountDTO> countCollaboratorsPerTaskByProjectId(@Param("projectId") Long projectId);
}
