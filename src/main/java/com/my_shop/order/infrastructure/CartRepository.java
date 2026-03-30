package com.my_shop.order.infrastructure;

import com.my_shop.order.domain.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Cart 엔티티용 Repository
 */
@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    /**
     * 사용자의 장바구니 목록 조회 (최신순)
     */
    List<Cart> findByUserSeqOrderByCreatedAtDesc(Long userSeq);

    /**
     * 사용자의 특정 상품+옵션 장바구니 항목 조회
     */
    Optional<Cart> findByUserSeqAndProductSeqAndProductOptionSeq(Long userSeq, Long productSeq, Long productOptionSeq);

    /**
     * 사용자의 특정 상품(옵션 없음) 장바구니 항목 조회
     */
    Optional<Cart> findByUserSeqAndProductSeqAndProductOptionIsNull(Long userSeq, Long productSeq);

    /**
     * 사용자의 장바구니 전체 삭제
     */
    void deleteByUserSeq(Long userSeq);

    /**
     * 사용자의 장바구니 항목 수 조회
     */
    long countByUserSeq(Long userSeq);
}
