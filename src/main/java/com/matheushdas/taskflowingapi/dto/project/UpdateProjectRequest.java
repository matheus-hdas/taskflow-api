package com.matheushdas.taskflowingapi.dto.project;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UpdateProjectRequest(
        @JsonProperty("name") String name,
        @JsonProperty("description") String description
) {}
