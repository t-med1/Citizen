package com.company.platform.service.implementations;

import com.company.platform.dto.CategoryDTO;
import com.company.platform.entity.Category;
import com.company.platform.exception.DuplicateResourceException;
import com.company.platform.exception.ResourceNotFoundException;
import com.company.platform.mapper.CategoryMapper;
import com.company.platform.repository.CategoryRepository;
import com.company.platform.service.interfaces.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryDTO> findAll() {
        return categoryRepository.findAll().stream().map(categoryMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public CategoryDTO findById(Long id) {
        return categoryMapper.toDTO(fetch(id));
    }

    @Override
    public CategoryDTO create(CategoryDTO dto) {
        if (categoryRepository.existsByName(dto.getName())) {
            throw new DuplicateResourceException("Une catégorie avec ce nom existe déjà : " + dto.getName());
        }
        Category category = categoryMapper.toEntity(dto);
        category.setId(null);
        return categoryMapper.toDTO(categoryRepository.save(category));
    }

    @Override
    public CategoryDTO update(Long id, CategoryDTO dto) {
        Category category = fetch(id);
        if (!category.getName().equals(dto.getName()) && categoryRepository.existsByName(dto.getName())) {
            throw new DuplicateResourceException("Une catégorie avec ce nom existe déjà : " + dto.getName());
        }
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        return categoryMapper.toDTO(categoryRepository.save(category));
    }

    @Override
    public void delete(Long id) {
        Category category = fetch(id);
        if (!category.getComplaints().isEmpty()) {
            throw new IllegalStateException("Impossible de supprimer une catégorie encore utilisée par des réclamations");
        }
        categoryRepository.delete(category);
    }

    @Override
    public long countAll() {
        return categoryRepository.count();
    }

    private Category fetch(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Catégorie", id));
    }
}
