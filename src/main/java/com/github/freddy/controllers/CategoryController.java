package com.github.freddy.controllers;

import com.github.freddy.dtos.CategoryInputDTO;
import com.github.freddy.dtos.CategoryDTO;
import com.github.freddy.dtos.CategoryPageDTO;
import com.github.freddy.services.CategoryService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/categories")
@AllArgsConstructor
public class CategoryController {
    private CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryDTO> createCategory(@RequestBody @Valid CategoryInputDTO category){
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createCategory(category));
    }

    @GetMapping
    public ResponseEntity<CategoryPageDTO> getAllCategories(@RequestParam int page, @RequestParam int size){
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.findAllCategories(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getCategory(@PathVariable UUID id){
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.findCategoryById(id));
    }

}
