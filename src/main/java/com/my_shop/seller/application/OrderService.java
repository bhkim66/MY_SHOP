package com.my_shop.seller.application;

import com.my_shop.market.domain.entity.Market;
import com.my_shop.market.infrastructure.MarketRepository;
import com.my_shop.order.domain.entity.Order;
import com.my_shop.order.domain.entity.OrderItem;
import com.my_shop.order.infrastructure.OrderItemRepository;
import com.my_shop.order.infrastructure.OrderRepository;
import com.my_shop.seller.interfaces.dto.RecentOrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * SELLER 주문 관리 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final MarketRepository marketRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    /**
     * 판매자의 최근 주문 조회 (최근 10건)
     *
     * @param sellerSeq 판매자 ID
     * @return 최근 주문 목록
     */
    public List<RecentOrderResponse> getRecentOrders(Long sellerSeq) {
        // 1. 판매자의 Market 조회
        Market market = marketRepository.findByOwnerSeq(sellerSeq)
                .orElseThrow(() -> new RuntimeException("판매자의 마켓을 찾을 수 없습니다."));

        // 2. 최근 10개 주문 조회
        List<Order> recentOrders = orderRepository.findTop10ByMarketSeqOrderByOrderedAtDesc(market.getSeq());

        if (recentOrders.isEmpty()) {
            return List.of();
        }

        // 3. 주문 ID 목록 추출
        List<Long> orderSeqs = recentOrders.stream()
                .map(Order::getSeq)
                .collect(Collectors.toList());

        // 4. 주문별 OrderItem 조회
        List<OrderItem> orderItems = orderItemRepository.findByOrderSeqIn(orderSeqs);

        // 5. 주문별로 그룹화
        Map<Long, List<OrderItem>> orderItemMap = orderItems.stream()
                .collect(Collectors.groupingBy(item -> item.getOrder().getSeq()));

        // 6. Response 생성
        return recentOrders.stream()
                .map(order -> {
                    List<OrderItem> items = orderItemMap.getOrDefault(order.getSeq(), List.of());
                    return RecentOrderResponse.from(order, items);
                })
                .collect(Collectors.toList());
    }
}
