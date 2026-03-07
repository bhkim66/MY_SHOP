package com.my_shop.product.infrastructure;

import com.my_shop.market.domain.entity.Market;
import com.my_shop.product.domain.entity.Category;
import com.my_shop.product.domain.entity.Product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Market으로 상품 목록 조회 (동적 검색 조건, 페이징)
     */
    @Query("SELECT p FROM Product p WHERE p.market = :market " +
           "AND (:productName IS NULL OR p.productName LIKE %:productName%) " +
           "AND (:status IS NULL OR p.status = :status) " +
           "AND (:categorySeq IS NULL OR p.category.seq = :categorySeq)")
    Page<Product> searchByCondition(
            @Param("market") Market market,
            @Param("productName") String productName,
            @Param("status") String status,
            @Param("categorySeq") Long categorySeq,
            Pageable pageable);

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
