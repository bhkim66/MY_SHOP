package com.my_shop.seller.interfaces.dto;

import com.my_shop.order.domain.entity.Order;
import com.my_shop.order.domain.entity.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 최근 주문 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecentOrderResponse {

    /**
     * 주문 ID
     */
    private Long orderId;

    /**
     * 주문 번호
     */
    private String orderNumber;

    /**
     * 주문자명
     */
    private String customerName;

    /**
     * 상품명 (첫 번째 상품명 + N개)
     */
    private String productName;

    /**
     * 총 수량
     */
    private Integer quantity;

    /**
     * 총 금액 (원)
     */
    private Integer totalPrice;

    /**
     * 주문 상태
     */
    private String status;

    /**
     * 주문 일시
     */
    private LocalDateTime createdAt;

    /**
     * Order와 OrderItem 목록으로부터 Response 생성
     */
    public static RecentOrderResponse from(Order order, List<OrderItem> items) {
        // 총 수량 계산
        int totalQty = items.stream()
                .mapToInt(OrderItem::getQty)
                .sum();

        // 상품명 생성 (첫 번째 상품명 + N개)
        String productName = "";
        if (!items.isEmpty()) {
            productName = items.get(0).getItemName();
            if (items.size() > 1) {
                productName += " 외 " + (items.size() - 1) + "개";
            }
        }

        return RecentOrderResponse.builder()
                .orderId(order.getSeq())
                .orderNumber(order.getOrderNo())
                .customerName(order.getBuyerName() != null ? order.getBuyerName() : "비회원")
                .productName(productName)
                .quantity(totalQty)
                .totalPrice(order.getTotalPayAmount())
                .status(order.getOrderStatus())
                .createdAt(order.getOrderedAt())
                .build();
    }
}
