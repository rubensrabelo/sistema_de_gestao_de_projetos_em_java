package com.management.project.repository;

import com.management.project.data.dto.project.ProjectTaskCountDTO;
import com.management.project.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Query(
            """
                SELECT
                    new com.management.project.data.dto.project.ProjectTaskCountDTO(p.id, p.name, COUNT(t))
                FROM Project p
                LEFT JOIN p.tasks t
                GROUP BY p.id, p.name
                ORDER BY COUNT(t) DESC
            """
    )
    List<ProjectTaskCountDTO> countTasksPerProject();
}
