package com.my_shop.product.interfaces.dto;

import com.my_shop.product.domain.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResponseDTO {

    private Long seq;
    private String categoryCode;
    private String categoryName;
    private int depth;
    private int sortOrder;

    public static CategoryResponseDTO of(Category category) {
        return CategoryResponseDTO.builder()
                .seq(category.getSeq())
                .categoryCode(category.getCategoryCode())
                .categoryName(category.getCategoryName())
                .depth(category.getDepth())
                .sortOrder(category.getSortOrder())
                .build();
    }
}
