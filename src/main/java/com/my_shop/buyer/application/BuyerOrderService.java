package com.my_shop.buyer.application;

import com.my_shop.buyer.interfaces.dto.*;
import com.my_shop.common.utils.OrderNumberGenerator;
import com.my_shop.member.domain.entity.User;
import com.my_shop.member.infrastructure.UserRepository;
import com.my_shop.order.domain.entity.Order;
import com.my_shop.order.domain.entity.OrderItem;
import com.my_shop.order.infrastructure.OrderItemRepository;
import com.my_shop.order.infrastructure.OrderRepository;
import com.my_shop.product.domain.entity.Product;
import com.my_shop.product.infrastructure.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BuyerOrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderNumberGenerator orderNumberGenerator;

    private static final int SHIPPING_FEE = 3000; // 기본 배송비

    /**
     * 주문 생성
     */
    @Transactional
    public OrderCreateResponse createOrder(OrderCreateRequest request, Long buyerSeq) {
        // 1. 구매자 조회
        User buyer = userRepository.findById(buyerSeq)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 2. 상품 조회 및 검증
        List<OrderCreateRequest.OrderItemRequest> itemRequests = request.getItems();
        if (itemRequests == null || itemRequests.isEmpty()) {
            throw new RuntimeException("최소 1개 이상의 상품이 필요합니다.");
        }

        List<Product> products = new ArrayList<>();
        int totalProductAmount = 0;

        for (OrderCreateRequest.OrderItemRequest itemReq : itemRequests) {
            Product product = productRepository.findById(itemReq.getProductSeq())
                    .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다. ID: " + itemReq.getProductSeq()));

            // 상품 상태 확인
            if (!"ON_SALE".equals(product.getStatus())) {
                throw new RuntimeException("현재 판매중인 상품이 아닙니다: " + product.getProductName());
            }

            // 재고 확인
            if (product.getStockQty() < itemReq.getQty()) {
                throw new RuntimeException("재고가 부족합니다: " + product.getProductName());
            }

            // 최소/최대 주문 수량 확인
            if (itemReq.getQty() < product.getMinOrderQty()) {
                throw new RuntimeException("최소 주문 수량 미만입니다: " + product.getProductName());
            }
            if (product.getMaxOrderQty() != null && itemReq.getQty() > product.getMaxOrderQty()) {
                throw new RuntimeException("최대 주문 수량 초과입니다: " + product.getProductName());
            }

            products.add(product);

            int unitPrice = product.getSalePrice() != null ? product.getSalePrice() : product.getPrice();
            totalProductAmount += unitPrice * itemReq.getQty();
        }

        // 3. 모든 상품이 같은 마켓인지 확인 (단일 마켓 주문 가정)
        Product firstProduct = products.get(0);
        for (Product p : products) {
            if (!p.getMarket().getSeq().equals(firstProduct.getMarket().getSeq())) {
                throw new RuntimeException("여러 마켓의 상품을 한 번에 주문할 수 없습니다.");
            }
        }

        // 4. 주문 생성
        int totalPayAmount = totalProductAmount + SHIPPING_FEE;
        String orderNo = orderNumberGenerator.generate();

        Order order = Order.create(
                orderNo,
                firstProduct.getMarket(),
                buyer,
                totalProductAmount,
                SHIPPING_FEE,
                totalPayAmount,
                request.getReceiverName(),
                request.getReceiverPhone(),
                request.getZipCode(),
                request.getAddress1(),
                request.getAddress2(),
                request.getShippingMessage()
        );

        Order savedOrder = orderRepository.save(order);

        // 5. 주문 상품 생성 및 재고 차감
        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            int qty = itemRequests.get(i).getQty();

            // 재고 차감
            product.decreaseStock(qty);

            // 주문 상품 생성
            OrderItem orderItem = OrderItem.create(savedOrder, product, qty);
            orderItemRepository.save(orderItem);
        }

        return OrderCreateResponse.from(savedOrder);
    }

    /**
     * 내 주문 목록 조회
     */
    @Transactional(readOnly = true)
    public Page<OrderListResponse> getMyOrders(Long buyerSeq, Pageable pageable) {
        Page<Order> orders = orderRepository.findByBuyerSeqOrderByOrderedAtDesc(buyerSeq, pageable);

        // 주문별 첫 번째 상품명과 상품 수 조회
        List<Long> orderSeqs = orders.getContent().stream()
                .map(Order::getSeq)
                .collect(Collectors.toList());

        List<OrderItem> allOrderItems = orderItemRepository.findByOrderSeqIn(orderSeqs);
        Map<Long, List<OrderItem>> orderItemsMap = allOrderItems.stream()
                .collect(Collectors.groupingBy(item -> item.getOrder().getSeq()));

        return orders.map(order -> {
            List<OrderItem> items = orderItemsMap.getOrDefault(order.getSeq(), List.of());
            String firstItemName = items.isEmpty() ? "" : items.get(0).getItemName();
            int itemCount = items.size();
            return OrderListResponse.of(order, firstItemName, itemCount);
        });
    }

    /**
     * 주문 상세 조회
     */
    @Transactional(readOnly = true)
    public OrderDetailResponse getOrderDetail(Long orderSeq, Long buyerSeq) {
        Order order = orderRepository.findById(orderSeq)
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다."));

        // 본인 주문인지 확인
        if (!order.getBuyer().getSeq().equals(buyerSeq)) {
            throw new RuntimeException("본인의 주문만 조회할 수 있습니다.");
        }

        List<OrderItem> orderItems = orderItemRepository.findByOrderSeq(orderSeq);
        return OrderDetailResponse.of(order, orderItems);
    }

    /**
     * 주문 취소
     */
    @Transactional
    public void cancelOrder(Long orderSeq, Long buyerSeq, String reason) {
        Order order = orderRepository.findById(orderSeq)
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다."));

        // 본인 주문인지 확인
        if (!order.getBuyer().getSeq().equals(buyerSeq)) {
            throw new RuntimeException("본인의 주문만 취소할 수 있습니다.");
        }

        // 주문 취소 (비즈니스 규칙 검증은 Order 엔티티에서 수행)
        order.cancel(reason);

        // 재고 복구
        List<OrderItem> orderItems = orderItemRepository.findByOrderSeq(orderSeq);
        for (OrderItem item : orderItems) {
            Product product = item.getProduct();
            product.increaseStock(item.getQty());
        }
    }
}
