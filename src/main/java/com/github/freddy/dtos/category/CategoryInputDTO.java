package com.github.freddy.dtos;

import jakarta.validation.constraints.NotBlank;

public record CategoryInputDTO(
        @NotBlank(message = "O nome da categoria não pode estar vazio")
        String name
) {
}
