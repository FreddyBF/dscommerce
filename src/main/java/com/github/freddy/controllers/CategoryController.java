package com.github.freddy.controllers;

import com.github.freddy.dtos.category.CategoryInputDTO;
import com.github.freddy.dtos.category.CategoryDTO;
import com.github.freddy.dtos.PageResponse;
import com.github.freddy.services.CategoryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/categories")
@AllArgsConstructor
public class CategoryController {

    private CategoryService categoryService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryDTO> createCategory(@RequestBody @Valid CategoryInputDTO category){
        CategoryDTO dto = categoryService.createCategory(category);
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/categories/{id}")
                .buildAndExpand(dto.id())
                .toUri();
        return ResponseEntity.created(location).body(dto);
    }

    @GetMapping
    public ResponseEntity<PageResponse<CategoryDTO>> getAllCategories(Pageable pageable){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(categoryService.findAllCategories(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getCategory(@PathVariable UUID id){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(categoryService.findCategoryById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryDTO> updateCategory(
            @PathVariable UUID id, @RequestBody @Valid CategoryInputDTO category
    ){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(categoryService.updateCategory(id, category));
    }

}
