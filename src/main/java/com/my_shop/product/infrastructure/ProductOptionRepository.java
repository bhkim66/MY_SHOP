package com.my_shop.product.infrastructure;

import com.my_shop.product.domain.entity.Product;
import com.my_shop.product.domain.entity.ProductOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductOptionRepository extends JpaRepository<ProductOption, Long> {
    List<ProductOption> findByProductOrderBySortOrderAsc(Product product);
}
