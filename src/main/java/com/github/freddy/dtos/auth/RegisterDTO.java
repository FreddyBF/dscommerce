package com.github.freddy.dtos.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterDTO(
        @NotBlank(message = "O nome é obrigatório")
        @Size(min= 4, message = "O nome deve ter no mínimo 4 caracteres")
        String name,

        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "E-mail inválido")
        String email,

        @NotBlank(message = "O e-mail é obrigatório")
        String phone,

        @NotBlank(message = "A senha é obrigatória")
        @Size(min = 4, message = "A senha deve ter no mínimo 4 caracteres")
        String password
) {
}
