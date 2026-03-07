package com.my_shop.seller.interfaces.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductUpdateRequest {

    private String productName;
    private String description;
    private Integer price;
    private Integer stockQty;
    private String status;
}
