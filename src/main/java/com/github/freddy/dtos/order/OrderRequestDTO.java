package com.github.freddy.dtos.order;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record OrderRequestDTO(
        @NotNull(message = "")
        UUID userId,
        @NotEmpty(message = "")
        List<OrderItem> items
) {

    public record OrderItem(
            UUID productId,
            Integer quantity
    ) {
    }
}
