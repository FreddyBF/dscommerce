package com.github.freddy.dtos;

import com.github.freddy.entity.Category;

import java.util.UUID;

public record CategoryDTO(
        UUID id,
        String name
) {
    public  CategoryDTO(Category category) {
        this(category.getId(), category.getName());
    }
}


