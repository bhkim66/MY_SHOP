package com.my_shop.payment.interfaces.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentConfirmResponse {

    private Long paymentSeq;
    private Long orderSeq;
    private String orderNo;
    private String payStatus;
    private Integer payAmount;
    private LocalDateTime approvedAt;
    private String message;
}
