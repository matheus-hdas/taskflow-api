package com.matheushdas.taskflowingapi.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record LoginResponse(
        @JsonProperty("username") String username,
        @JsonProperty("authenticated") Boolean authenticated,
        @JsonProperty("generated_at") LocalDateTime generatedAt,
        @JsonProperty("expiration") LocalDateTime expiration,
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("refresh_token") String refreshToken
) {}

