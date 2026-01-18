package com.github.freddy.services;

import com.github.freddy.dtos.PageResponse;
import com.github.freddy.dtos.product.ProductInputDTO;
import com.github.freddy.dtos.product.ProductOutputDTO;
import com.github.freddy.entity.Category;
import com.github.freddy.entity.Product;
import com.github.freddy.exceptions.ResourceNotFoundException;
import com.github.freddy.repositories.CategoryRepository;
import com.github.freddy.repositories.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public ProductOutputDTO create(ProductInputDTO dto) {
        Product entity = new Product();
        entity.setName(dto.name());
        entity.setPrice(dto.price());
        entity.setDescription(dto.description());
        entity.setImgUrl(dto.imgUrl());

        // Buscar as categorias pelo UUID e associar ao produto
        if (dto.categoryIds() != null && !dto.categoryIds().isEmpty()) {
            Set<Category> categories = new HashSet<>();
            for (UUID categoryId : dto.categoryIds()) {
                Category category = categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada "));
                categories.add(category);
            }
            entity.setCategories(categories);
        }
        entity = productRepository.save(entity);
        return  new  ProductOutputDTO(entity);
    }

    @Transactional(readOnly = true)
    public ProductOutputDTO findById(UUID id) {
        Product product = productRepository.
                findById(id).orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado "));
        return new ProductOutputDTO(product);
    }

    @Transactional(readOnly = true)
    public PageResponse<ProductOutputDTO> findAll(Pageable pageable) {

        Page<Product>result = productRepository.findAll(pageable);
        Page<ProductOutputDTO> dtoPage = result.map(ProductOutputDTO::new);
        return PageResponse.fromPage(dtoPage);
    }

    public List<ProductOutputDTO> searchProductByName(String name) {
        return productRepository
                .findByProductNameIgnoreCaseContaining(name)
                .stream()
                .map(ProductOutputDTO::new)
                .toList();
    }

    @Transactional
    public void delete(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));
        productRepository.delete(product);
    }

    @Transactional
    public ProductOutputDTO update(UUID id, ProductInputDTO dto) {
        // Busca o produto existente
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));
        product.setName(dto.name());
        product.setDescription(dto.description());
        product.setPrice(dto.price());
        product.setImgUrl(dto.imgUrl());

        // Actualiza categorias
        if (dto.categoryIds() != null && !dto.categoryIds().isEmpty()) {
            Set<Category> categories = dto.categoryIds().stream()
                    .map(catId -> categoryRepository.findById(catId)
                            .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada: ")))
                    .collect(Collectors.toSet());
            product.setCategories(categories);
        } else {
            product.getCategories().clear(); // Se não vier categorias, limpa
        }

        product = productRepository.save(product);

        //Retorna DTO de saída
        return new ProductOutputDTO(product);
    }

}
