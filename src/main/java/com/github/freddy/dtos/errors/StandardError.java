package com.github.freddy.dtos.errors;

import java.time.Instant;

public record StandardError(
        Instant timestamp,
        Integer status,
        String message,
        String details

) {
}
