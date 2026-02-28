package com.my_shop.order.infrastructure;

import com.my_shop.order.domain.entity.OrderStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * OrderStatusHistory Repository
 */
@Repository
public interface OrderStatusHistoryRepository extends JpaRepository<OrderStatusHistory, Long> {

    /**
     * 주문별 상태 이력 조회 (생성일시 내림차순)
     */
    List<OrderStatusHistory> findByOrderSeqOrderByCreatedAtDesc(Long orderSeq);

    /**
     * 주문별 상태 이력 조회 (생성일시 오름차순)
     */
    List<OrderStatusHistory> findByOrderSeqOrderByCreatedAtAsc(Long orderSeq);
}
