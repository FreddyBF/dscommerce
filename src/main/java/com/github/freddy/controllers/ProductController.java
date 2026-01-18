package com.github.freddy.controllers;


import com.github.freddy.dtos.PageResponse;
import com.github.freddy.dtos.product.ProductInputDTO;
import com.github.freddy.dtos.product.ProductOutputDTO;
import com.github.freddy.services.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
@AllArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductOutputDTO> createProduct(@RequestBody ProductInputDTO productInputDTO) {
        ProductOutputDTO dto = productService.create(productInputDTO);
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/products/{id}")
                .buildAndExpand(dto.id())
                .toUri();
        return ResponseEntity.created(location).body(dto);
    }

    @GetMapping
    public ResponseEntity<PageResponse<ProductOutputDTO>> getAllProducts(Pageable pageable) {
        return ResponseEntity.ok().body(productService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductOutputDTO> findById(@PathVariable  UUID id) {
        return ResponseEntity.ok().body(productService.findById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductOutputDTO>  updateProduct(@PathVariable UUID id, @RequestBody ProductInputDTO productInputDTO) {
        return ResponseEntity.ok().body(productService.update(id, productInputDTO));
    }
}
