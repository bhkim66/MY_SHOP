package com.my_shop.product.application;

import com.my_shop.product.domain.entity.Category;
import com.my_shop.product.infrastructure.CategoryRepository;
import com.my_shop.product.interfaces.dto.CategoryResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    /**
     * 전체 카테고리 목록 조회 (표시 가능한 카테고리만)
     */
    @Transactional(readOnly = true)
    public List<CategoryResponseDTO> getAllCategories() {
        List<Category> categories = categoryRepository.findByIsDisplayOrderBySortOrderAsc(true);
        return categories.stream()
                .map(CategoryResponseDTO::of)
                .collect(Collectors.toList());
    }
}
