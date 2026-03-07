package com.my_shop.buyer.interfaces.dto;

import com.my_shop.product.domain.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BuyerProductListResponse {

    private Long seq;
    private String productName;
    private Integer price;
    private Integer salePrice;
    private String thumbnailUrl;
    private String marketName;
    private String categoryName;
    private Integer reviewCount;
    private BigDecimal ratingAvg;
    private Integer saleCount;

    public static BuyerProductListResponse from(Product product) {
        return BuyerProductListResponse.builder()
                .seq(product.getSeq())
                .productName(product.getProductName())
                .price(product.getPrice())
                .salePrice(product.getSalePrice())
                .thumbnailUrl(product.getThumbnailUrl())
                .marketName(product.getMarket().getMarketName())
                .categoryName(product.getCategory() != null ? product.getCategory().getCategoryName() : null)
                .reviewCount(product.getReviewCount())
                .ratingAvg(product.getRatingAvg())
                .saleCount(product.getSaleCount())
                .build();
    }
}
