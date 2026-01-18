package com.github.freddy.dtos.user;

import java.util.UUID;

public record SignupResponseDTO(
        UUID id,
        String name,
        String email,
        String phone
) {
}
