package com.github.freddy.dtos;

import java.time.Instant;
import java.util.List;

public record ValidationError(
        Instant timestamp,
        Integer status,
        String message,
        String path,
        List<FieldMessage> errors
) {
    // Record auxiliar para cada campo específico
    public record FieldMessage(String fieldName, String message) {}
}
