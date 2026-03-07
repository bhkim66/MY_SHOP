package com.my_shop.payment.interfaces.dto;

import com.my_shop.payment.domain.entity.Payment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {

    private Long paymentSeq;
    private Long orderSeq;
    private String orderNo;
    private String payMethod;
    private String payStatus;
    private Integer payAmount;
    private String pgProvider;
    private String pgTid;
    private String cardCompany;
    private String cardNumber;
    private Integer installmentMonth;
    private LocalDateTime approvedAt;
    private String receiptUrl;

    public static PaymentResponse from(Payment payment) {
        return PaymentResponse.builder()
                .paymentSeq(payment.getSeq())
                .orderSeq(payment.getOrder().getSeq())
                .orderNo(payment.getOrder().getOrderNo())
                .payMethod(payment.getPayMethod())
                .payStatus(payment.getPayStatus())
                .payAmount(payment.getPayAmount())
                .pgProvider(payment.getPgProvider())
                .pgTid(payment.getPgTid())
                .cardCompany(payment.getCardCompany())
                .cardNumber(payment.getCardNumber())
                .installmentMonth(payment.getInstallmentMonth())
                .approvedAt(payment.getApprovedAt())
                .receiptUrl(payment.getReceiptUrl())
                .build();
    }
}
