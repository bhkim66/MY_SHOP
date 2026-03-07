package com.my_shop.buyer.interfaces.dto;

import com.my_shop.product.domain.entity.Product;
import com.my_shop.product.domain.entity.ProductImage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BuyerProductDetailResponse {

    private Long seq;
    private String productName;
    private String description;
    private Integer price;
    private Integer salePrice;
    private Integer stockQty;
    private Integer minOrderQty;
    private Integer maxOrderQty;
    private String status;
    private String thumbnailUrl;
    private List<String> imageUrls;
    private String marketName;
    private Long marketSeq;
    private String categoryName;
    private String categoryCode;
    private Integer reviewCount;
    private BigDecimal ratingAvg;
    private Integer viewCount;
    private Integer saleCount;

    public static BuyerProductDetailResponse of(Product product, List<ProductImage> images) {
        return BuyerProductDetailResponse.builder()
                .seq(product.getSeq())
                .productName(product.getProductName())
                .description(product.getDescription())
                .price(product.getPrice())
                .salePrice(product.getSalePrice())
                .stockQty(product.getStockQty())
                .minOrderQty(product.getMinOrderQty())
                .maxOrderQty(product.getMaxOrderQty())
                .status(product.getStatus())
                .thumbnailUrl(product.getThumbnailUrl())
                .imageUrls(images.stream()
                        .map(ProductImage::getImageUrl)
                        .collect(Collectors.toList()))
                .marketName(product.getMarket().getMarketName())
                .marketSeq(product.getMarket().getSeq())
                .categoryName(product.getCategory() != null ? product.getCategory().getCategoryName() : null)
                .categoryCode(product.getCategory() != null ? product.getCategory().getCategoryCode() : null)
                .reviewCount(product.getReviewCount())
                .ratingAvg(product.getRatingAvg())
                .viewCount(product.getViewCount())
                .saleCount(product.getSaleCount())
                .build();
    }
}
