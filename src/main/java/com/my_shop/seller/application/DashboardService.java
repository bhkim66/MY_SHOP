package com.my_shop.seller.application;

import com.my_shop.market.domain.entity.Market;
import com.my_shop.market.infrastructure.MarketRepository;
import com.my_shop.order.infrastructure.OrderRepository;
import com.my_shop.product.infrastructure.ProductRepository;
import com.my_shop.seller.interfaces.dto.DashboardStatsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * SELLER 대시보드 통계 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final MarketRepository marketRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    /**
     * 판매자 대시보드 통계 조회
     *
     * @param sellerSeq 판매자 ID
     * @return 대시보드 통계
     */
    public DashboardStatsResponse getDashboardStats(Long sellerSeq) {
        // 1. 판매자의 Market 조회
        Market market = marketRepository.findByOwnerSeq(sellerSeq)
                .orElseThrow(() -> new RuntimeException("판매자의 마켓을 찾을 수 없습니다."));

        Long marketSeq = market.getSeq();

        // 2. 전체 통계
        long totalSales = orderRepository.sumTotalPayAmountByMarketSeq(marketSeq);
        long totalOrders = orderRepository.countByMarketSeq(marketSeq);
        long pendingOrders = orderRepository.countByMarketSeqAndOrderStatus(marketSeq, "PENDING");
        long totalProducts = productRepository.countByMarketSeq(marketSeq);

        // 3. 오늘 통계 (00:00:00부터)
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        long todaySales = orderRepository.sumTotalPayAmountByMarketSeqAndOrderedAtAfter(marketSeq, todayStart);
        long todayOrders = orderRepository.countByMarketSeqAndOrderedAtAfter(marketSeq, todayStart);

        // 4. 이번 달 통계 (1일 00:00:00부터)
        LocalDateTime monthStart = LocalDateTime.of(LocalDate.now().withDayOfMonth(1), LocalTime.MIN);
        long monthSales = orderRepository.sumTotalPayAmountByMarketSeqAndOrderedAtAfter(marketSeq, monthStart);
        long monthOrders = orderRepository.countByMarketSeqAndOrderedAtAfter(marketSeq, monthStart);

        return DashboardStatsResponse.builder()
                .totalSales(totalSales)
                .totalOrders((int) totalOrders)
                .pendingOrders((int) pendingOrders)
                .totalProducts((int) totalProducts)
                .todaySales(todaySales)
                .todayOrders((int) todayOrders)
                .monthSales(monthSales)
                .monthOrders((int) monthOrders)
                .build();
    }
}
