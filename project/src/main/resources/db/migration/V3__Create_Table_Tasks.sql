CREATE TABLE tb_tasks (
                          id SERIAL PRIMARY KEY,
                          name VARCHAR(100) NOT NULL,
                          status VARCHAR(50) NOT NULL DEFAULT 'NOT_DONE',
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          project_id INT NOT NULL,
                          CONSTRAINT fk_project FOREIGN KEY (project_id) REFERENCES tb_projects (id) ON DELETE CASCADE
);
