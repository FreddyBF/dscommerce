package com.github.freddy.dtos;

public record Pagination(
        boolean first,
        boolean last,
        boolean previous,
        int next,
        int page,
        long totalElements
) {
}
