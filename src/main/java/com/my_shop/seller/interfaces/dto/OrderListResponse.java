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
 * 판매자 주문 목록 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderListResponse {

    /**
     * 주문 ID
     */
    private Long orderSeq;

    /**
     * 주문 번호
     */
    private String orderNo;

    /**
     * 주문자명
     */
    private String buyerName;

    /**
     * 수령인명
     */
    private String receiverName;

    /**
     * 수령인 연락처
     */
    private String receiverPhone;

    /**
     * 상품명 (첫 번째 상품명 + N개)
     */
    private String productName;

    /**
     * 총 수량
     */
    private Integer totalQuantity;

    /**
     * 총 결제 금액
     */
    private Integer totalPayAmount;

    /**
     * 주문 상태
     */
    private String orderStatus;

    /**
     * 주문 일시
     */
    private LocalDateTime orderedAt;

    /**
     * Order와 OrderItem 목록으로부터 Response 생성
     */
    public static OrderListResponse from(Order order, List<OrderItem> items) {
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

        return OrderListResponse.builder()
                .orderSeq(order.getSeq())
                .orderNo(order.getOrderNo())
                .buyerName(order.getBuyerName() != null ? order.getBuyerName() : "비회원")
                .receiverName(order.getReceiverName())
                .receiverPhone(order.getReceiverPhone())
                .productName(productName)
                .totalQuantity(totalQty)
                .totalPayAmount(order.getTotalPayAmount())
                .orderStatus(order.getOrderStatus())
                .orderedAt(order.getOrderedAt())
                .build();
    }
}
