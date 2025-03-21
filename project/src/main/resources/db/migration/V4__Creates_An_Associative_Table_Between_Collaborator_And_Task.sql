CREATE TABLE task_collaborator (
                                   task_id INT NOT NULL,
                                   collaborator_id INT NOT NULL,
                                   PRIMARY KEY (task_id, collaborator_id),
                                   CONSTRAINT fk_task FOREIGN KEY (task_id) REFERENCES tb_tasks(id) ON DELETE CASCADE,
                                   CONSTRAINT fk_collaborator FOREIGN KEY (collaborator_id) REFERENCES tb_collaborators(id) ON DELETE CASCADE
);
