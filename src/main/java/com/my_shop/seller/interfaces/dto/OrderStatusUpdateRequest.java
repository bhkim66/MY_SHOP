package com.my_shop.seller.interfaces.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 주문 상태 변경 요청 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusUpdateRequest {

    /**
     * 변경할 주문 상태
     * PREPARING: 상품 준비중
     * SHIPPING: 배송중
     * DELIVERED: 배송 완료
     */
    @NotBlank(message = "주문 상태는 필수입니다.")
    @Pattern(regexp = "^(PREPARING|SHIPPING|DELIVERED)$",
             message = "유효하지 않은 주문 상태입니다. (PREPARING, SHIPPING, DELIVERED 중 하나)")
    private String status;

    /**
     * 상태 변경 사유 (선택)
     */
    private String reason;

    /**
     * 배송 회사명 (SHIPPING 상태로 변경 시 필수)
     */
    private String shippingCompany;

    /**
     * 송장번호 (SHIPPING 상태로 변경 시 필수)
     */
    private String trackingNumber;
}
