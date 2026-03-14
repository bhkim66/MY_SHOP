package com.my_shop.seller.interfaces.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

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

    private List<OptionRequest> options; // 선택사항

    @Getter
    @Setter
    @NoArgsConstructor
    public static class OptionRequest {
        private String optionGroup;      // 옵션 그룹명 (예: 색상, 사이즈)
        private String optionValue;      // 옵션 값 (예: 빨강, L)
        private Integer additionalPrice; // 추가 금액 (기본값 0)
        private Integer stockQty;        // 옵션별 재고
    }
}
