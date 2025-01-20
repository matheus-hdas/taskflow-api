package com.matheushdas.taskflowingapi.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record RegisterResponse(
        @JsonProperty("id") UUID id,
        @JsonProperty("username") String username,
        @JsonProperty("email") String email
) {
}
