package com.my_shop.order.infrastructure;

import com.my_shop.order.domain.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Order 엔티티용 Repository
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Market별 주문 조회 (페이징)
     */
    Page<Order> findByMarketSeq(Long marketSeq, Pageable pageable);

    /**
     * Market별 최근 주문 조회 (Top N)
     */
    List<Order> findTop10ByMarketSeqOrderByOrderedAtDesc(Long marketSeq);

    /**
     * Market별 특정 상태의 주문 수 카운트
     */
    long countByMarketSeqAndOrderStatus(Long marketSeq, String orderStatus);

    /**
     * Market별 전체 주문 수
     */
    long countByMarketSeq(Long marketSeq);

    /**
     * Market별 총 매출액 계산
     */
    @Query("SELECT COALESCE(SUM(o.totalPayAmount), 0) FROM Order o WHERE o.market.seq = :marketSeq")
    Long sumTotalPayAmountByMarketSeq(@Param("marketSeq") Long marketSeq);

    /**
     * Market별 특정 날짜 이후 총 매출액 계산
     */
    @Query("SELECT COALESCE(SUM(o.totalPayAmount), 0) FROM Order o WHERE o.market.seq = :marketSeq AND o.orderedAt >= :startDate")
    Long sumTotalPayAmountByMarketSeqAndOrderedAtAfter(
            @Param("marketSeq") Long marketSeq,
            @Param("startDate") LocalDateTime startDate);

    /**
     * Market별 특정 날짜 이후 주문 수 카운트
     */
    long countByMarketSeqAndOrderedAtAfter(Long marketSeq, LocalDateTime startDate);

    /**
     * Market별 특정 기간 내 주문 조회
     */
    List<Order> findByMarketSeqAndOrderedAtBetween(
            Long marketSeq,
            LocalDateTime startDate,
            LocalDateTime endDate);

    /**
     * 구매자별 주문 목록 조회 (페이징)
     */
    Page<Order> findByBuyerSeqOrderByOrderedAtDesc(Long buyerSeq, Pageable pageable);

    /**
     * 주문번호로 주문 조회
     */
    Optional<Order> findByOrderNo(String orderNo);

    /**
     * Market별 주문 조회 - 상태 필터링 (페이징)
     */
    Page<Order> findByMarketSeqAndOrderStatus(Long marketSeq, String orderStatus, Pageable pageable);

    /**
     * Market별 주문 조회 - 기간 필터링 (페이징)
     */
    Page<Order> findByMarketSeqAndOrderedAtBetween(Long marketSeq, LocalDateTime startDate,
                                                    LocalDateTime endDate, Pageable pageable);

    /**
     * Market별 주문 조회 - 상태 & 기간 필터링 (페이징)
     */
    Page<Order> findByMarketSeqAndOrderStatusAndOrderedAtBetween(Long marketSeq, String orderStatus,
                                                                  LocalDateTime startDate, LocalDateTime endDate,
                                                                  Pageable pageable);
}
