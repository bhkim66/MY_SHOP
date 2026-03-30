package com.my_shop.seller.application;

import com.my_shop.market.domain.entity.Market;
import com.my_shop.market.infrastructure.MarketRepository;
import com.my_shop.member.domain.entity.MemberRole;
import com.my_shop.member.domain.entity.User;
import com.my_shop.order.infrastructure.OrderRepository;
import com.my_shop.product.infrastructure.ProductRepository;
import com.my_shop.seller.interfaces.dto.DashboardStatsResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
@DisplayName("DashboardService 단위 테스트")
class DashboardServiceTest {

    @InjectMocks
    private DashboardService dashboardService;

    @Mock
    private MarketRepository marketRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    private User seller;
    private Market market;

    @BeforeEach
    void setUp() {
        seller = User.create("seller@test.com", "encoded", "판매자", "seller@test.com", "01011112222", MemberRole.SELLER);
        ReflectionTestUtils.setField(seller, "seq", 1L);

        market = Market.create(seller, "테스트마켓", "test-market", "테스트 마켓입니다.", "ACTIVE");
        ReflectionTestUtils.setField(market, "seq", 10L);
    }

    // ========== getDashboardStats ==========

    @Nested
    @DisplayName("대시보드 통계 조회")
    class GetDashboardStats {

        @Test
        @DisplayName("성공: 각 통계 수치가 올바르게 매핑된다")
        void getDashboardStats_success() {
            // given
            given(marketRepository.findByOwnerSeq(1L)).willReturn(Optional.of(market));

            given(orderRepository.sumTotalPayAmountByMarketSeq(10L)).willReturn(1000000L);
            given(orderRepository.countByMarketSeq(10L)).willReturn(100L);
            given(orderRepository.countByMarketSeqAndOrderStatus(10L, "PENDING")).willReturn(5L);
            given(productRepository.countByMarketSeq(10L)).willReturn(30L);

            // 오늘 통계
            given(orderRepository.sumTotalPayAmountByMarketSeqAndOrderedAtAfter(eq(10L), any(LocalDateTime.class)))
                    .willReturn(50000L)   // 첫 번째 호출: 오늘
                    .willReturn(300000L); // 두 번째 호출: 이번 달
            given(orderRepository.countByMarketSeqAndOrderedAtAfter(eq(10L), any(LocalDateTime.class)))
                    .willReturn(5L)  // 첫 번째 호출: 오늘
                    .willReturn(30L); // 두 번째 호출: 이번 달

            // when
            DashboardStatsResponse response = dashboardService.getDashboardStats(1L);

            // then
            assertThat(response.getTotalSales()).isEqualTo(1000000L);
            assertThat(response.getTotalOrders()).isEqualTo(100);
            assertThat(response.getPendingOrders()).isEqualTo(5);
            assertThat(response.getTotalProducts()).isEqualTo(30);
            assertThat(response.getTodaySales()).isEqualTo(50000L);
            assertThat(response.getTodayOrders()).isEqualTo(5);
            assertThat(response.getMonthSales()).isEqualTo(300000L);
            assertThat(response.getMonthOrders()).isEqualTo(30);
        }

        @Test
        @DisplayName("성공: 매출이 전혀 없는 경우에도 정상적으로 0을 반환한다")
        void getDashboardStats_success_zero_sales() {
            // given
            given(marketRepository.findByOwnerSeq(1L)).willReturn(Optional.of(market));

            given(orderRepository.sumTotalPayAmountByMarketSeq(10L)).willReturn(0L);
            given(orderRepository.countByMarketSeq(10L)).willReturn(0L);
            given(orderRepository.countByMarketSeqAndOrderStatus(10L, "PENDING")).willReturn(0L);
            given(productRepository.countByMarketSeq(10L)).willReturn(0L);

            given(orderRepository.sumTotalPayAmountByMarketSeqAndOrderedAtAfter(eq(10L), any(LocalDateTime.class)))
                    .willReturn(0L);
            given(orderRepository.countByMarketSeqAndOrderedAtAfter(eq(10L), any(LocalDateTime.class)))
                    .willReturn(0L);

            // when
            DashboardStatsResponse response = dashboardService.getDashboardStats(1L);

            // then
            assertThat(response.getTotalSales()).isZero();
            assertThat(response.getTotalOrders()).isZero();
            assertThat(response.getPendingOrders()).isZero();
            assertThat(response.getTotalProducts()).isZero();
            assertThat(response.getTodaySales()).isZero();
            assertThat(response.getTodayOrders()).isZero();
            assertThat(response.getMonthSales()).isZero();
            assertThat(response.getMonthOrders()).isZero();
        }

        @Test
        @DisplayName("실패: 마켓이 없는 판매자 조회 시 예외가 발생한다")
        void getDashboardStats_fail_no_market() {
            // given
            given(marketRepository.findByOwnerSeq(1L)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> dashboardService.getDashboardStats(1L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("판매자의 마켓을 찾을 수 없습니다.");
        }

        @Test
        @DisplayName("성공: 대기 주문이 없는 경우 pendingOrders는 0이다")
        void getDashboardStats_success_no_pending_orders() {
            // given
            given(marketRepository.findByOwnerSeq(1L)).willReturn(Optional.of(market));

            given(orderRepository.sumTotalPayAmountByMarketSeq(10L)).willReturn(500000L);
            given(orderRepository.countByMarketSeq(10L)).willReturn(50L);
            given(orderRepository.countByMarketSeqAndOrderStatus(10L, "PENDING")).willReturn(0L);
            given(productRepository.countByMarketSeq(10L)).willReturn(10L);

            given(orderRepository.sumTotalPayAmountByMarketSeqAndOrderedAtAfter(eq(10L), any(LocalDateTime.class)))
                    .willReturn(10000L);
            given(orderRepository.countByMarketSeqAndOrderedAtAfter(eq(10L), any(LocalDateTime.class)))
                    .willReturn(1L);

            // when
            DashboardStatsResponse response = dashboardService.getDashboardStats(1L);

            // then
            assertThat(response.getPendingOrders()).isZero();
        }
    }
}
