package com.matheushdas.taskflowingapi.dto.project;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.matheushdas.taskflowingapi.model.entity.Task;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class ProjectResponse extends RepresentationModel<ProjectResponse> {
    @JsonProperty("id")
    private UUID key;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("status")
    private String status;

    @JsonProperty("tasks")
    private List<Task> tasks;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
}
