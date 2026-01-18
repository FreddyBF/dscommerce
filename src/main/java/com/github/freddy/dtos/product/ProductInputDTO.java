package com.github.freddy.dtos.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

public record ProductInputDTO(

        @NotBlank(message = "O nome do produto não pode estar vazio")
        String name,

        String description,

        @NotNull(message = "O preço é obrigatório")
        @Positive(message = "O preço deve ser maior que zero")
        BigDecimal price,

        String imgUrl,

        @NotEmpty(message = "O produto deve ter pelo menos uma categoria")
        Set<UUID> categoryIds

) {}
