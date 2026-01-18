package com.github.freddy.dtos.product;

import com.github.freddy.dtos.category.CategoryDTO;
import com.github.freddy.entity.Product;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public record ProductOutputDTO(
        UUID id,
        String name,
        String description,
        BigDecimal price,
        String imgUrl,
        Set<CategoryDTO> categories
) {
    public ProductOutputDTO(Product product) {
        this(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getImgUrl(),
                product.getCategories() == null ? Set.of() :
                        product.getCategories()
                                .stream()
                                .map(cat -> new CategoryDTO(
                                        cat.getId(),
                                        cat.getName())
                                )
                                .collect(Collectors.toSet())
        );
    }
}
