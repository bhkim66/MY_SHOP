package com.my_shop.seller.interfaces.dto;

import com.my_shop.delivery.domain.entity.Shipment;
import com.my_shop.order.domain.entity.Order;
import com.my_shop.order.domain.entity.OrderItem;
import com.my_shop.order.domain.entity.OrderStatusHistory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 판매자 주문 상세 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailResponse {

    /**
     * 주문 ID
     */
    private Long orderSeq;

    /**
     * 주문 번호
     */
    private String orderNo;

    /**
     * 주문 상태
     */
    private String orderStatus;

    /**
     * 주문 일시
     */
    private LocalDateTime orderedAt;

    /**
     * 주문자 정보
     */
    private BuyerInfo buyerInfo;

    /**
     * 수령인 정보
     */
    private ReceiverInfo receiverInfo;

    /**
     * 주문 상품 목록
     */
    private List<OrderItemInfo> orderItems;

    /**
     * 결제 정보
     */
    private PaymentInfo paymentInfo;

    /**
     * 배송 정보
     */
    private ShipmentInfo shipmentInfo;

    /**
     * 주문 상태 이력
     */
    private List<StatusHistoryInfo> statusHistory;

    /**
     * 주문자 정보
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BuyerInfo {
        private String name;
        private String email;
        private String phone;
    }

    /**
     * 수령인 정보
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReceiverInfo {
        private String name;
        private String phone;
        private String zipCode;
        private String address1;
        private String address2;
        private String shippingMessage;
    }

    /**
     * 주문 상품 정보
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemInfo {
        private Long itemSeq;
        private String itemName;
        private String itemOption;
        private Integer unitPrice;
        private Integer quantity;
        private Integer itemAmount;
        private String itemStatus;
    }

    /**
     * 결제 정보
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentInfo {
        private Integer totalProductAmount;
        private Integer totalDiscountAmount;
        private Integer shippingFee;
        private Integer totalPayAmount;
    }

    /**
     * 배송 정보
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShipmentInfo {
        private String shippingCompany;
        private String trackingNumber;
        private String shippingStatus;
        private LocalDateTime shippedAt;
        private LocalDateTime deliveredAt;
    }

    /**
     * 상태 이력 정보
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatusHistoryInfo {
        private String previousStatus;
        private String newStatus;
        private String changedReason;
        private LocalDateTime createdAt;
    }

    /**
     * Order, OrderItem, Shipment, StatusHistory로부터 Response 생성
     */
    public static OrderDetailResponse from(Order order, List<OrderItem> items,
                                          Shipment shipment, List<OrderStatusHistory> history) {
        return OrderDetailResponse.builder()
                .orderSeq(order.getSeq())
                .orderNo(order.getOrderNo())
                .orderStatus(order.getOrderStatus())
                .orderedAt(order.getOrderedAt())
                .buyerInfo(BuyerInfo.builder()
                        .name(order.getBuyerName())
                        .email(order.getBuyerEmail())
                        .phone(order.getBuyerPhone())
                        .build())
                .receiverInfo(ReceiverInfo.builder()
                        .name(order.getReceiverName())
                        .phone(order.getReceiverPhone())
                        .zipCode(order.getZipCode())
                        .address1(order.getAddress1())
                        .address2(order.getAddress2())
                        .shippingMessage(order.getShippingMessage())
                        .build())
                .orderItems(items.stream()
                        .map(item -> OrderItemInfo.builder()
                                .itemSeq(item.getSeq())
                                .itemName(item.getItemName())
                                .itemOption(item.getItemOption())
                                .unitPrice(item.getUnitPrice())
                                .quantity(item.getQty())
                                .itemAmount(item.getItemAmount())
                                .itemStatus(item.getItemStatus())
                                .build())
                        .collect(Collectors.toList()))
                .paymentInfo(PaymentInfo.builder()
                        .totalProductAmount(order.getTotalProductAmount())
                        .totalDiscountAmount(order.getTotalDiscountAmount())
                        .shippingFee(order.getShippingFee())
                        .totalPayAmount(order.getTotalPayAmount())
                        .build())
                .shipmentInfo(shipment != null ? ShipmentInfo.builder()
                        .shippingCompany(shipment.getShippingCompany())
                        .trackingNumber(shipment.getTrackingNumber())
                        .shippingStatus(shipment.getShippingStatus())
                        .shippedAt(shipment.getShippedAt())
                        .deliveredAt(shipment.getDeliveredAt())
                        .build() : null)
                .statusHistory(history.stream()
                        .map(h -> StatusHistoryInfo.builder()
                                .previousStatus(h.getPreviousStatus())
                                .newStatus(h.getNewStatus())
                                .changedReason(h.getChangedReason())
                                .createdAt(h.getCreatedAt())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}
