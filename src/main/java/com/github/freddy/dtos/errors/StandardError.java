package com.github.freddy.dtos;

import java.time.Instant;

public record StandardError(
        Instant timestamp,
        Integer status,
        String message,
        String path

) {
}
