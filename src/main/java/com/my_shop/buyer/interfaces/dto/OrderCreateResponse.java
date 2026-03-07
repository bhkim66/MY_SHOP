package com.my_shop.buyer.interfaces.dto;

import com.my_shop.order.domain.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderCreateResponse {

    private Long orderSeq;
    private String orderNo;
    private Integer totalPayAmount;
    private String orderStatus;

    public static OrderCreateResponse from(Order order) {
        return OrderCreateResponse.builder()
                .orderSeq(order.getSeq())
                .orderNo(order.getOrderNo())
                .totalPayAmount(order.getTotalPayAmount())
                .orderStatus(order.getOrderStatus())
                .build();
    }
}
