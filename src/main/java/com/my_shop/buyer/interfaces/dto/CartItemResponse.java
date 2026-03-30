package com.my_shop.buyer.interfaces.dto;

import com.my_shop.order.domain.entity.Cart;
import com.my_shop.product.domain.entity.Product;
import com.my_shop.product.domain.entity.ProductOption;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CartItemResponse {

    private Long cartSeq;
    private Long productSeq;
    private String productName;
    private String thumbnailUrl;
    private Long productOptionSeq;
    private String optionName;  // "색상: 빨강 / 사이즈: L" 형태
    private int price;
    private int qty;
    private int stockQty;
    private int totalPrice;

    /**
     * Cart 엔티티로부터 CartItemResponse 생성
     */
    public static CartItemResponse from(Cart cart) {
        Product product = cart.getProduct();
        ProductOption option = cart.getProductOption();

        int effectivePrice = product.getSalePrice() != null ? product.getSalePrice() : product.getPrice();
        int stockQty = product.getStockQty();
        String optionName = null;
        Long productOptionSeq = null;

        if (option != null) {
            effectivePrice += option.getAdditionalPrice();
            stockQty = option.getStockQty();
            optionName = option.getOptionGroup() + ": " + option.getOptionValue();
            productOptionSeq = option.getSeq();
        }

        return CartItemResponse.builder()
                .cartSeq(cart.getSeq())
                .productSeq(product.getSeq())
                .productName(product.getProductName())
                .thumbnailUrl(product.getThumbnailUrl())
                .productOptionSeq(productOptionSeq)
                .optionName(optionName)
                .price(effectivePrice)
                .qty(cart.getQty())
                .stockQty(stockQty)
                .totalPrice(effectivePrice * cart.getQty())
                .build();
    }
}
