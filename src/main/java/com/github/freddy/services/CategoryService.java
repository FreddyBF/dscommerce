package com.github.freddy.services;

import com.github.freddy.dtos.category.CategoryInputDTO;
import com.github.freddy.dtos.category.CategoryDTO;
import com.github.freddy.dtos.PageResponse;
import com.github.freddy.entity.Category;
import com.github.freddy.exceptions.ConflictException;
import com.github.freddy.exceptions.ResourceNotFoundException;
import com.github.freddy.repositories.CategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.UUID;

@Service
@AllArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    //Criar uma nova categoria
    @Transactional
    public CategoryDTO createCategory(CategoryInputDTO categoryInputDTO) {
        if(categoryRepository.existsByNameIgnoreCase(categoryInputDTO.name()))
            throw new ConflictException("Category already exists");
        Category category = new Category();
        category.setName(categoryInputDTO.name().toLowerCase(Locale.ROOT));
        category = categoryRepository.save(category);
        return new CategoryDTO(
                category.getId(),
                category.getName()
        );
    }

    //Listar todas as categorias
    @Transactional(readOnly = true)
    public PageResponse<CategoryDTO> findAllCategories(Pageable pageable) {

        Page<Category> categories = categoryRepository.findAll(pageable);

        Page<CategoryDTO> dto = categories.map(CategoryDTO::new);

        return PageResponse.fromPage(dto);
    }

    //Buscar a categoria pelo ID
    public CategoryDTO findCategoryById(UUID id) {
        Category entity = categoryRepository
                .findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        return new CategoryDTO(
                entity.getId(),
                entity.getName()
        );
    }

    //Acualizar a categoria
    @Transactional
    public CategoryDTO updateCategory(UUID id, CategoryInputDTO categoryInputDTO) {
        Category category = categoryRepository
                .findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        category.setName(categoryInputDTO.name());
        return new CategoryDTO(category);
    }
}
