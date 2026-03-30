package com.my_shop.buyer.interfaces.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class CartResponse {

    private List<CartItemResponse> items;
    private int totalProductAmount;
    private int shippingFee;
    private int totalPayAmount;
    private int itemCount;

    private static final int SHIPPING_FEE = 3000;

    /**
     * CartItemResponse 목록으로부터 CartResponse 생성
     */
    public static CartResponse of(List<CartItemResponse> items) {
        int totalProductAmount = items.stream()
                .mapToInt(CartItemResponse::getTotalPrice)
                .sum();

        int shippingFee = items.isEmpty() ? 0 : SHIPPING_FEE;
        int totalPayAmount = totalProductAmount + shippingFee;

        return CartResponse.builder()
                .items(items)
                .totalProductAmount(totalProductAmount)
                .shippingFee(shippingFee)
                .totalPayAmount(totalPayAmount)
                .itemCount(items.size())
                .build();
    }
}
