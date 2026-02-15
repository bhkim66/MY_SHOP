package com.my_shop.product.infrastructure;

import com.my_shop.product.domain.entity.Product;
import com.my_shop.product.domain.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    /**
     * 상품의 이미지 목록 조회 (정렬 순서대로)
     */
    List<ProductImage> findByProductOrderBySortOrderAsc(Product product);

    /**
     * 상품과 이미지 타입으로 조회 (정렬 순서대로)
     */
    List<ProductImage> findByProductAndImageTypeOrderBySortOrderAsc(Product product, String imageType);

    /**
     * 상품의 메인 이미지만 조회
     */
    List<ProductImage> findByProductAndImageType(Product product, String imageType);
}
