package com.github.freddy.dtos;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
public record PaymentDTO(
        UUID id,
        UUID orderId,
        BigDecimal amount,
        Instant date
) {
}
