package com.my_shop.payment.domain;

import com.my_shop.common.entity.BaseEntity;
import com.my_shop.order.domain.Order;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments", indexes = {
        @Index(name = "idx_payment_order", columnList = "order_seq"),
        @Index(name = "idx_payment_pg_tid", columnList = "pg_tid"),
        @Index(name = "idx_payment_status_date", columnList = "pay_status, approved_at")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq")
    private Long seq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_seq", nullable = false)
    private Order order;

    @Column(name = "pay_method", nullable = false, length = 20)
    private String payMethod;

    @Column(name = "pay_status", nullable = false, length = 20)
    private String payStatus;

    @Column(name = "pay_amount", nullable = false)
    private int payAmount;

    @Column(name = "pg_provider", length = 50)
    private String pgProvider;

    @Column(name = "pg_tid", length = 100)
    private String pgTid;

    @Column(name = "card_company", length = 50)
    private String cardCompany;

    @Column(name = "card_number", length = 20)
    private String cardNumber;

    @Column(name = "installment_month")
    private Integer installmentMonth;

    @Column(name = "vbank_name", length = 50)
    private String vbankName;

    @Column(name = "vbank_number", length = 50)
    private String vbankNumber;

    @Column(name = "vbank_expires_at")
    private LocalDateTime vbankExpiresAt;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "canceled_at")
    private LocalDateTime canceledAt;

    @Column(name = "refunded_at")
    private LocalDateTime refundedAt;

    @Column(name = "fail_reason", length = 500)
    private String failReason;

    @Column(name = "receipt_url", length = 500)
    private String receiptUrl;
}
