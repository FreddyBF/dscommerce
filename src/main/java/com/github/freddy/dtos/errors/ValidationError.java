package com.github.freddy.dtos.errors;

import java.time.Instant;
import java.util.List;

public record ValidationError(
        Instant timestamp,
        Integer status,
        String message,
        String details,
        List<FieldMessage> errors
) {
    // Record auxiliar para cada campo específico
    public record FieldMessage(String fieldName, String message) {}
}
