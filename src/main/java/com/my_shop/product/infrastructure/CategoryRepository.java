package com.my_shop.product.infrastructure;

import com.my_shop.product.domain.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * 카테고리 코드로 조회
     */
    Optional<Category> findByCategoryCode(String categoryCode);

    /**
     * 표시 여부로 카테고리 목록 조회 (정렬 순서대로)
     */
    List<Category> findByIsDisplayOrderBySortOrderAsc(boolean isDisplay);

    /**
     * 부모 카테고리로 하위 카테고리 목록 조회
     */
    List<Category> findByParentOrderBySortOrderAsc(Category parent);
}
