package com.matheushdas.taskflowingapi.dto.project;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record UpdateProjectRequest(
        @JsonProperty("id") UUID id,
        @JsonProperty("name") String name,
        @JsonProperty("description") String description
) { }
