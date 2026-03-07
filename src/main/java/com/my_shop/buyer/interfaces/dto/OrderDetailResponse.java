package com.my_shop.buyer.interfaces.dto;

import com.my_shop.order.domain.entity.Order;
import com.my_shop.order.domain.entity.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDetailResponse {

    private Long orderSeq;
    private String orderNo;
    private String orderStatus;

    // 금액 정보
    private Integer totalProductAmount;
    private Integer totalDiscountAmount;
    private Integer couponDiscountAmount;
    private Integer shippingFee;
    private Integer totalPayAmount;

    // 배송 정보
    private String receiverName;
    private String receiverPhone;
    private String zipCode;
    private String address1;
    private String address2;
    private String shippingMessage;

    // 주문자 정보
    private String buyerName;
    private String buyerEmail;
    private String buyerPhone;

    // 마켓 정보
    private String marketName;

    // 일시 정보
    private LocalDateTime orderedAt;
    private LocalDateTime confirmedAt;
    private LocalDateTime canceledAt;
    private String cancelReason;

    // 주문 상품 목록
    private List<OrderItemDto> items;

    public static OrderDetailResponse of(Order order, List<OrderItem> orderItems) {
        return OrderDetailResponse.builder()
                .orderSeq(order.getSeq())
                .orderNo(order.getOrderNo())
                .orderStatus(order.getOrderStatus())
                .totalProductAmount(order.getTotalProductAmount())
                .totalDiscountAmount(order.getTotalDiscountAmount())
                .couponDiscountAmount(order.getCouponDiscountAmount())
                .shippingFee(order.getShippingFee())
                .totalPayAmount(order.getTotalPayAmount())
                .receiverName(order.getReceiverName())
                .receiverPhone(order.getReceiverPhone())
                .zipCode(order.getZipCode())
                .address1(order.getAddress1())
                .address2(order.getAddress2())
                .shippingMessage(order.getShippingMessage())
                .buyerName(order.getBuyerName())
                .buyerEmail(order.getBuyerEmail())
                .buyerPhone(order.getBuyerPhone())
                .marketName(order.getMarket().getMarketName())
                .orderedAt(order.getOrderedAt())
                .confirmedAt(order.getConfirmedAt())
                .canceledAt(order.getCanceledAt())
                .cancelReason(order.getCancelReason())
                .items(orderItems.stream()
                        .map(OrderItemDto::from)
                        .collect(Collectors.toList()))
                .build();
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderItemDto {
        private Long seq;
        private Long productSeq;
        private String itemName;
        private String itemOption;
        private Integer unitPrice;
        private Integer qty;
        private Integer itemAmount;
        private Integer discountAmount;
        private String itemStatus;
        private String thumbnailUrl;

        public static OrderItemDto from(OrderItem item) {
            return OrderItemDto.builder()
                    .seq(item.getSeq())
                    .productSeq(item.getProduct().getSeq())
                    .itemName(item.getItemName())
                    .itemOption(item.getItemOption())
                    .unitPrice(item.getUnitPrice())
                    .qty(item.getQty())
                    .itemAmount(item.getItemAmount())
                    .discountAmount(item.getDiscountAmount())
                    .itemStatus(item.getItemStatus())
                    .thumbnailUrl(item.getProduct().getThumbnailUrl())
                    .build();
        }
    }
}
