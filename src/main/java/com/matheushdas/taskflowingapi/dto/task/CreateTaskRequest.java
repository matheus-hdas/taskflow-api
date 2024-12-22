package com.matheushdas.taskflowingapi.dto.task;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record CreateTaskRequest(
        @JsonProperty("name") String name,
        @JsonProperty("description") String description,
        @JsonProperty("project_id") UUID projectId
) {}
