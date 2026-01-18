package com.github.freddy.dtos;

import org.springframework.data.domain.Page;
import java.util.List;
public record PageResponse<T>(
        List<T> data,
        PaginationMetadata pagination
) {
    // Record interno para os metadados
    public record PaginationMetadata(
            int page,
            int size,
            long totalElements,
            int totalPages,
            boolean hasNext,
            boolean hasPrevious
    ) {}

    public static <T> PageResponse<T> fromPage(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                new PaginationMetadata(
                        page.getNumber(),
                        page.getSize(),
                        page.getTotalElements(),
                        page.getTotalPages(),
                        page.hasNext(),
                        page.hasPrevious()
                )
        );
    }
}