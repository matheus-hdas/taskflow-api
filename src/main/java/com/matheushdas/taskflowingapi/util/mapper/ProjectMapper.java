package com.matheushdas.taskflowingapi.util.mapper;

import com.matheushdas.taskflowingapi.dto.project.CreateProjectRequest;
import com.matheushdas.taskflowingapi.dto.project.ProjectResponse;
import com.matheushdas.taskflowingapi.dto.project.UpdateProjectRequest;
import com.matheushdas.taskflowingapi.model.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class ProjectMapper {
    public Project toEntity(CreateProjectRequest data) {
        return new Project(
                data.name(),
                data.description()
        );
    }

    public Project toEntity(UpdateProjectRequest data) {
        return new Project(
                data.id(),
                data.name(),
                data.description()
        );
    }

    public ProjectResponse toResponse(Project data) {
        return new ProjectResponse(
                data.getId(),
                data.getName(),
                data.getDescription(),
                data.getStatus(),
                data.getTasks(),
                data.getCreatedAt(),
                data.getUpdatedAt()
        );
    }

    public Page<ProjectResponse> toResponsePage(Page<Project> data) {
        return data.map(this::toResponse);
    }
}
