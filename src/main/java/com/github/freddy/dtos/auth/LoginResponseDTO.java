package com.github.freddy.dtos.user;

import java.util.List;
import java.util.UUID;

public record LoginResponseDTO(
        String accessToken,
        String refreshToken,
        String tokenType,
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
