package com.my_shop.seller.interfaces.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 대시보드 통계 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsResponse {

    /**
     * 총 매출액 (원)
     */
    private Long totalSales;

    /**
     * 총 주문 수
     */
    private Integer totalOrders;

    /**
     * 처리 대기 주문 수
     */
    private Integer pendingOrders;

    /**
     * 등록 상품 수
     */
    private Integer totalProducts;

    /**
     * 오늘 매출액 (원)
     */
    private Long todaySales;

    /**
     * 오늘 주문 수
     */
    private Integer todayOrders;

    /**
     * 이번 달 매출액 (원)
     */
    private Long monthSales;

    /**
     * 이번 달 주문 수
     */
    private Integer monthOrders;
}
