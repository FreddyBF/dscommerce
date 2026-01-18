package com.github.freddy.dtos.auth;

import java.util.List;
import java.util.UUID;

public record LoginResponseDTO(
        String access_token,
        String refresh_token,
        String token_type,
        UserSummaryDTO user
) {
    public LoginResponseDTO(String accessToken, String refreshToken, UserSummaryDTO user) {
        this(accessToken, refreshToken, "Bearer", user);
    }

    public record UserSummaryDTO(
            UUID id,
            String email,
            List<String> roles
    ) {}
}
