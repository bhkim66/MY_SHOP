package com.my_shop.seller.application;

import com.my_shop.delivery.domain.entity.Shipment;
import com.my_shop.delivery.infrastructure.ShipmentRepository;
import com.my_shop.market.domain.entity.Market;
import com.my_shop.market.infrastructure.MarketRepository;
import com.my_shop.order.domain.entity.Order;
import com.my_shop.order.domain.entity.OrderItem;
import com.my_shop.order.domain.entity.OrderStatusHistory;
import com.my_shop.order.infrastructure.OrderItemRepository;
import com.my_shop.order.infrastructure.OrderRepository;
import com.my_shop.order.infrastructure.OrderStatusHistoryRepository;
import com.my_shop.seller.interfaces.dto.OrderDetailResponse;
import com.my_shop.seller.interfaces.dto.OrderListResponse;
import com.my_shop.seller.interfaces.dto.RecentOrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    private final OrderStatusHistoryRepository orderStatusHistoryRepository;
    private final ShipmentRepository shipmentRepository;

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

    /**
     * 판매자 주문 목록 조회 (페이징, 필터링)
     *
     * @param sellerSeq 판매자 ID
     * @param status 주문 상태 필터 (선택)
     * @param startDate 시작 날짜 (선택)
     * @param endDate 종료 날짜 (선택)
     * @param pageable 페이징 정보
     * @return 주문 목록 (페이징)
     */
    public Page<OrderListResponse> getOrders(Long sellerSeq, String status,
                                             LocalDateTime startDate, LocalDateTime endDate,
                                             Pageable pageable) {
        // 1. 판매자의 Market 조회
        Market market = getMarketBySellerSeq(sellerSeq);

        // 2. 조건에 따라 주문 조회
        Page<Order> orders = getOrdersByCondition(market.getSeq(), status, startDate, endDate, pageable);

        if (orders.isEmpty()) {
            return Page.empty(pageable);
        }

        // 3. 주문 ID 목록 추출
        List<Long> orderSeqs = orders.stream()
                .map(Order::getSeq)
                .collect(Collectors.toList());

        // 4. 주문별 OrderItem 조회
        List<OrderItem> orderItems = orderItemRepository.findByOrderSeqIn(orderSeqs);

        // 5. 주문별로 그룹화
        Map<Long, List<OrderItem>> orderItemMap = orderItems.stream()
                .collect(Collectors.groupingBy(item -> item.getOrder().getSeq()));

        // 6. Response 생성
        return orders.map(order -> {
            List<OrderItem> items = orderItemMap.getOrDefault(order.getSeq(), List.of());
            return OrderListResponse.from(order, items);
        });
    }

    /**
     * 판매자 주문 상세 조회
     *
     * @param sellerSeq 판매자 ID
     * @param orderSeq 주문 ID
     * @return 주문 상세 정보
     */
    public OrderDetailResponse getOrderDetail(Long sellerSeq, Long orderSeq) {
        // 1. 판매자의 Market 조회
        Market market = getMarketBySellerSeq(sellerSeq);

        // 2. 주문 조회 및 권한 검증
        Order order = orderRepository.findById(orderSeq)
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다."));

        validateOrderOwnership(order, market.getSeq());

        // 3. 주문 상품 조회
        List<OrderItem> orderItems = orderItemRepository.findByOrderSeqIn(List.of(orderSeq));

        // 4. 배송 정보 조회
        Shipment shipment = shipmentRepository.findByOrderSeq(orderSeq).orElse(null);

        // 5. 주문 상태 이력 조회
        List<OrderStatusHistory> statusHistory = orderStatusHistoryRepository
                .findByOrderSeqOrderByCreatedAtAsc(orderSeq);

        // 6. Response 생성
        return OrderDetailResponse.from(order, orderItems, shipment, statusHistory);
    }

    /**
     * 주문 상태 변경
     *
     * @param sellerSeq 판매자 ID
     * @param orderSeq 주문 ID
     * @param newStatus 새로운 주문 상태
     * @param reason 변경 사유
     * @param shippingCompany 배송 회사명 (SHIPPING 상태 변경 시)
     * @param trackingNumber 송장번호 (SHIPPING 상태 변경 시)
     */
    @Transactional
    public void updateOrderStatus(Long sellerSeq, Long orderSeq, String newStatus,
                                  String reason, String shippingCompany, String trackingNumber) {
        // 1. 판매자의 Market 조회
        Market market = getMarketBySellerSeq(sellerSeq);

        // 2. 주문 조회 및 권한 검증
        Order order = orderRepository.findById(orderSeq)
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다."));

        validateOrderOwnership(order, market.getSeq());

        // 3. 상태 전환 유효성 검증
        validateStatusTransition(order.getOrderStatus(), newStatus);

        // 4. SHIPPING 상태로 변경 시 배송 정보 필수 검증
        if ("SHIPPING".equals(newStatus)) {
            if (shippingCompany == null || shippingCompany.isBlank() ||
                trackingNumber == null || trackingNumber.isBlank()) {
                throw new RuntimeException("배송 중 상태로 변경하려면 배송 정보(배송 회사, 송장번호)가 필요합니다.");
            }
            // 배송 정보 등록/수정
            registerOrUpdateShipment(order, shippingCompany, trackingNumber);
        }

        // 5. 주문 상태 변경
        String previousStatus = order.getOrderStatus();
        order.updateStatus(newStatus);
        orderRepository.save(order);

        // 6. 상태 이력 저장
        OrderStatusHistory history = OrderStatusHistory.create(
                order, previousStatus, newStatus, sellerSeq, reason
        );
        orderStatusHistoryRepository.save(history);

        // 7. 배송 상태 업데이트
        if ("SHIPPING".equals(newStatus)) {
            Shipment shipment = shipmentRepository.findByOrderSeq(orderSeq)
                    .orElseThrow(() -> new RuntimeException("배송 정보를 찾을 수 없습니다."));
            shipment.startShipping();
            shipmentRepository.save(shipment);
        } else if ("DELIVERED".equals(newStatus)) {
            Shipment shipment = shipmentRepository.findByOrderSeq(orderSeq)
                    .orElse(null);
            if (shipment != null) {
                shipment.completeDelivery();
                shipmentRepository.save(shipment);
            }
        }
    }

    /**
     * 배송 정보 등록/수정
     *
     * @param sellerSeq 판매자 ID
     * @param orderSeq 주문 ID
     * @param shippingCompany 배송 회사명
     * @param trackingNumber 송장번호
     */
    @Transactional
    public void registerOrUpdateShipment(Long sellerSeq, Long orderSeq,
                                        String shippingCompany, String trackingNumber) {
        // 1. 판매자의 Market 조회
        Market market = getMarketBySellerSeq(sellerSeq);

        // 2. 주문 조회 및 권한 검증
        Order order = orderRepository.findById(orderSeq)
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다."));

        validateOrderOwnership(order, market.getSeq());

        // 3. 배송 정보 등록/수정
        registerOrUpdateShipment(order, shippingCompany, trackingNumber);
    }

    /**
     * 배송 정보 등록/수정 (내부 메서드)
     */
    private void registerOrUpdateShipment(Order order, String shippingCompany, String trackingNumber) {
        Shipment shipment = shipmentRepository.findByOrderSeq(order.getSeq())
                .orElse(null);

        if (shipment == null) {
            // 배송 정보가 없으면 새로 생성
            shipment = Shipment.create(order, shippingCompany, trackingNumber);
        } else {
            // 배송 정보가 있으면 업데이트
            shipment.updateShippingInfo(shippingCompany, trackingNumber);
        }

        shipmentRepository.save(shipment);
    }

    /**
     * 판매자의 Market 조회
     */
    private Market getMarketBySellerSeq(Long sellerSeq) {
        return marketRepository.findByOwnerSeq(sellerSeq)
                .orElseThrow(() -> new RuntimeException("판매자의 마켓을 찾을 수 없습니다."));
    }

    /**
     * 조건에 따라 주문 조회
     */
    private Page<Order> getOrdersByCondition(Long marketSeq, String status,
                                            LocalDateTime startDate, LocalDateTime endDate,
                                            Pageable pageable) {
        boolean hasStatus = status != null && !status.isBlank();
        boolean hasDateRange = startDate != null && endDate != null;

        if (hasStatus && hasDateRange) {
            return orderRepository.findByMarketSeqAndOrderStatusAndOrderedAtBetween(
                    marketSeq, status, startDate, endDate, pageable);
        } else if (hasStatus) {
            return orderRepository.findByMarketSeqAndOrderStatus(marketSeq, status, pageable);
        } else if (hasDateRange) {
            return orderRepository.findByMarketSeqAndOrderedAtBetween(
                    marketSeq, startDate, endDate, pageable);
        } else {
            return orderRepository.findByMarketSeq(marketSeq, pageable);
        }
    }

    /**
     * 주문 소유권 검증 (판매자의 마켓 주문인지 확인)
     */
    private void validateOrderOwnership(Order order, Long marketSeq) {
        if (!order.getMarket().getSeq().equals(marketSeq)) {
            throw new RuntimeException("해당 주문에 대한 접근 권한이 없습니다.");
        }
    }

    /**
     * 주문 상태 전환 유효성 검증
     */
    private void validateStatusTransition(String currentStatus, String newStatus) {
        // PAYMENT_COMPLETED → PREPARING
        if ("PAYMENT_COMPLETED".equals(currentStatus) && "PREPARING".equals(newStatus)) {
            return;
        }
        // PREPARING → SHIPPING
        if ("PREPARING".equals(currentStatus) && "SHIPPING".equals(newStatus)) {
            return;
        }
        // SHIPPING → DELIVERED
        if ("SHIPPING".equals(currentStatus) && "DELIVERED".equals(newStatus)) {
            return;
        }

        throw new RuntimeException(
                String.format("잘못된 상태 전환입니다. 현재 상태: %s, 변경하려는 상태: %s",
                        currentStatus, newStatus)
        );
    }
}
