package com.matheushdas.taskflowingapi.dto.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.matheushdas.taskflowingapi.model.entity.Project;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({"id", "name", "description", "status", "inserted_at", "_links"})
public class TaskResponse extends RepresentationModel<TaskResponse> {
    @JsonProperty("id")
    private UUID key;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("status")
    private String status;

    @JsonProperty("inserted_at")
    private Project project;
}
