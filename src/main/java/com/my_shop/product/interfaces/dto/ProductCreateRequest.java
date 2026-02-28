package com.my_shop.product.interfaces.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductCreateRequest {

    @NotBlank(message = "상품명은 필수입니다")
    private String productName;

    @NotNull(message = "가격은 필수입니다")
    private Integer price;

    @NotBlank(message = "상품 설명은 필수입니다")
    private String description;

    @NotBlank(message = "카테고리 코드는 필수입니다")
    private String categoryCode;

    @NotNull(message = "재고 수량은 필수입니다")
    private Integer stockQty;

    private Integer minOrderQty; // 선택사항 (기본값 1)
}
