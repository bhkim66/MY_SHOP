package com.my_shop.product.infrastructure;

import com.my_shop.market.domain.entity.Market;
import com.my_shop.product.domain.entity.Category;
import com.my_shop.product.domain.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Market으로 상품 목록 조회 (페이징)
     */
    Page<Product> findByMarket(Market market, Pageable pageable);

    /**
     * Market과 상태로 상품 목록 조회 (페이징)
     */
    Page<Product> findByMarketAndStatus(Market market, String status, Pageable pageable);

    /**
     * 카테고리와 상태로 상품 목록 조회 (페이징)
     */
    Page<Product> findByCategoryAndStatus(Category category, String status, Pageable pageable);

    /**
     * 상태로 상품 목록 조회 (페이징)
     */
    Page<Product> findByStatus(String status, Pageable pageable);

    /**
     * Market ID로 상품 수 카운트
     */
    long countByMarketSeq(Long marketSeq);
}
