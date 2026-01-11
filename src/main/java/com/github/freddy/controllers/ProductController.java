package com.github.freddy.controllers;


import com.github.freddy.dtos.ProductInputDTO;
import com.github.freddy.dtos.ProductOutputDTO;
import com.github.freddy.dtos.ProductPageDTO;
import com.github.freddy.services.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/products")
@AllArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<ProductPageDTO> getAllProducts(@RequestParam int page, @RequestParam int limit) {
        Pageable pageable = PageRequest.of(page, limit);
        return ResponseEntity.ok().body(productService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductOutputDTO> findById(@PathVariable  UUID id) {
        return ResponseEntity.ok().body(productService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ProductOutputDTO> createProduct(@RequestBody ProductInputDTO productInputDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.insert(productInputDTO));
    }
}
