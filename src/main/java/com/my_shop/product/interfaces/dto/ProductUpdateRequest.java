package com.my_shop.product.interfaces.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductUpdateRequest {

    private String productName;
    private Integer price;
    private String description;
    private Integer stockQty;
    private String status; // ON_SALE, SOLD_OUT, HIDDEN
}
