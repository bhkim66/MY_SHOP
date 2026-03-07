package com.my_shop.buyer.interfaces.dto;

import com.my_shop.order.domain.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderListResponse {

    private Long orderSeq;
    private String orderNo;
    private String orderStatus;
    private Integer totalPayAmount;
    private String marketName;
    private LocalDateTime orderedAt;
    private String firstItemName;
    private Integer itemCount;

    public static OrderListResponse of(Order order, String firstItemName, int itemCount) {
        return OrderListResponse.builder()
                .orderSeq(order.getSeq())
                .orderNo(order.getOrderNo())
                .orderStatus(order.getOrderStatus())
                .totalPayAmount(order.getTotalPayAmount())
                .marketName(order.getMarket().getMarketName())
                .orderedAt(order.getOrderedAt())
                .firstItemName(firstItemName)
                .itemCount(itemCount)
                .build();
    }
}
