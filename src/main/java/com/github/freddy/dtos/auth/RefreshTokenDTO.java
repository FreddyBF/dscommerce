package com.github.freddy.dtos.auth;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenDTO(
        @NotBlank(message = "O Refresh Token é obrigatório")
        String refreshToken
) {
}
