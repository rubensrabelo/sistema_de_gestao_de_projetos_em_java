package com.management.project.repository;

import com.management.project.data.dto.collaborator.CollaboratorTaskCount;
import com.management.project.model.Collaborator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CollaboratorRepository extends JpaRepository<Collaborator, Long> {

    boolean existsByEmail(String email);

    @Query(
            """
                SELECT
                  new com.management.project.data.dto.collaborator.CollaboratorTaskCount(c.id, c.name, COUNT(t))
                FROM Collaborator c
                LEFT JOIN c.tasks t
                GROUP BY c.id, c.name
                ORDER BY COUNT(t) DESC
            """
    )
    List<CollaboratorTaskCount> countTasksPerCollaborator();
}
