CREATE TABLE tb_task (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    project_id UUID NOT NULL,
    description TEXT,
    status VARCHAR(12) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT tb_task_project_id_fkey FOREIGN KEY (project_id)
    REFERENCES tb_project(id)
    ON DELETE CASCADE
);