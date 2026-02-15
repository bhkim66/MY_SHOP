package com.my_shop.product.interfaces.dto;

import com.my_shop.product.domain.entity.Product;
import com.my_shop.product.domain.entity.ProductImage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {

    private Long seq;
    private String productName;
    private Integer price;
    private String description;
    private String categoryName;
    private String categoryCode;
    private Integer stockQty;
    private String status;
    private String thumbnailUrl;
    private List<String> imageUrls;
    private Integer viewCount;
    private Integer saleCount;
    private String marketName;

    public static ProductResponse of(Product product, List<ProductImage> images) {
        return ProductResponse.builder()
                .seq(product.getSeq())
                .productName(product.getProductName())
                .price(product.getPrice())
                .description(product.getDescription())
                .categoryName(product.getCategory() != null ? product.getCategory().getCategoryName() : null)
                .categoryCode(product.getCategory() != null ? product.getCategory().getCategoryCode() : null)
                .stockQty(product.getStockQty())
                .status(product.getStatus())
                .thumbnailUrl(product.getThumbnailUrl())
                .imageUrls(images.stream()
                        .map(ProductImage::getImageUrl)
                        .collect(Collectors.toList()))
                .viewCount(product.getViewCount())
                .saleCount(product.getSaleCount())
                .marketName(product.getMarket().getMarketName())
                .build();
    }
}
