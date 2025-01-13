package com.matheushdas.taskflowingapi.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LoginRequest(
        @JsonProperty("login") String login,
        @JsonProperty("password") String password
) {}
