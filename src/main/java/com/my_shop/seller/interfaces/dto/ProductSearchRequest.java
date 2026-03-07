package com.my_shop.seller.interfaces.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductSearchRequest {

    private String productName;
    private String status;
    private String categorySeq;
}
