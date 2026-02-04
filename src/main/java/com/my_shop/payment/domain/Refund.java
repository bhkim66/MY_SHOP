package com.my_shop.payment.domain;

import com.my_shop.common.entity.BaseEntity;
import com.my_shop.order.domain.Order;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "refunds", indexes = {
        @Index(name = "idx_refund_payment", columnList = "payment_seq"),
        @Index(name = "idx_refund_order", columnList = "order_seq"),
        @Index(name = "idx_refund_status", columnList = "refund_status")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Refund extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq")
    private Long seq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_seq", nullable = false)
    private Payment payment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_seq", nullable = false)
    private Order order;

    @Column(name = "refund_amount", nullable = false)
    private int refundAmount;

    @Column(name = "refund_reason", nullable = false, length = 500)
    private String refundReason;

    @Column(name = "refund_status", nullable = false, length = 20)
    private String refundStatus;

    @Column(name = "bank_name", length = 50)
    private String bankName;

    @Column(name = "account_number", length = 50)
    private String accountNumber;

    @Column(name = "account_holder", length = 50)
    private String accountHolder;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "rejected_at")
    private LocalDateTime rejectedAt;

    @Column(name = "reject_reason", length = 500)
    private String rejectReason;
}
