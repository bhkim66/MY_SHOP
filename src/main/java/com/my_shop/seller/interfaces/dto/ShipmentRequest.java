package com.my_shop.seller.interfaces.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 배송 정보 등록/수정 요청 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentRequest {

    /**
     * 배송 회사명
     */
    @NotBlank(message = "배송 회사명은 필수입니다.")
    private String shippingCompany;

    /**
     * 송장번호
     */
    @NotBlank(message = "송장번호는 필수입니다.")
    private String trackingNumber;
}
