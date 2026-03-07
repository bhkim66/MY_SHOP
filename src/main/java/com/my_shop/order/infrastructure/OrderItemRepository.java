package com.my_shop.order.infrastructure;

import com.my_shop.order.domain.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * OrderItem 엔티티용 Repository
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    /**
     * 주문 ID로 주문 상품 목록 조회
     */
    List<OrderItem> findByOrderSeq(Long orderSeq);

    /**
     * 여러 주문 ID로 주문 상품 목록 조회
     */
    List<OrderItem> findByOrderSeqIn(List<Long> orderSeqs);
}
