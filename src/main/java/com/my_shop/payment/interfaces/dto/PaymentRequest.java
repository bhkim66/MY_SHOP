package com.my_shop.payment.interfaces.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PaymentRequest {

    @NotNull(message = "주문 번호는 필수입니다.")
    private Long orderSeq;

    @NotBlank(message = "결제 수단은 필수입니다.")
    private String payMethod; // CARD, TRANSFER, VBANK, etc.

    @NotNull(message = "결제 금액은 필수입니다.")
    @Positive(message = "결제 금액은 0보다 커야 합니다.")
    private Integer payAmount;

    // 카드 결제 시 추가 정보 (Mock용)
    private String cardCompany;
    private String cardNumber;
    private Integer installmentMonth;
}
