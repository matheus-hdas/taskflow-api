package com.matheushdas.taskflowingapi.util.mapper;

import com.matheushdas.taskflowingapi.dto.task.CreateTaskRequest;
import com.matheushdas.taskflowingapi.dto.task.TaskResponse;
import com.matheushdas.taskflowingapi.model.entity.Task;
import com.matheushdas.taskflowingapi.persistence.ProjectRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class TaskMapper {
    private ProjectRepository projectRepository;

    public Task toEntity(CreateTaskRequest data) {
        return new Task(
                data.name(),
                data.description(),
                projectRepository.findById(data.projectId())
                        .orElseThrow()
        );
    }

    public TaskResponse toResponse(Task data) {
        return new TaskResponse(
                data.getId(),
                data.getName(),
                data.getDescription(),
                data.getStatus(),
                data.getProject()
        );
    }

    public Page<TaskResponse> toResponsePage(Page<Task> data) {
        return data.map(this::toResponse);
    }

}
