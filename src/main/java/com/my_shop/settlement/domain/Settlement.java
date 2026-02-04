package com.my_shop.settlement.domain;

import com.my_shop.common.entity.BaseEntity;
import com.my_shop.market.domain.Market;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "settlements", indexes = {
        @Index(name = "idx_settlement_market_status", columnList = "market_seq, status"),
        @Index(name = "idx_settlement_period", columnList = "settlement_period_start, settlement_period_end"),
        @Index(name = "idx_settlement_status_confirmed", columnList = "status, confirmed_at")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Settlement extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq")
    private Long seq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "market_seq", nullable = false)
    private Market market;

    @Column(name = "settlement_period_start", nullable = false)
    private LocalDateTime settlementPeriodStart;

    @Column(name = "settlement_period_end", nullable = false)
    private LocalDateTime settlementPeriodEnd;

    @Column(name = "total_pay_amount", nullable = false)
    private int totalPayAmount;

    @Column(name = "total_refund_amount", nullable = false)
    private int totalRefundAmount;

    @Column(name = "fee_amount", nullable = false)
    private int feeAmount;

    @Column(name = "settlement_amount", nullable = false)
    private int settlementAmount;

    @Column(name = "status", nullable = false, length = 20)
    private String status; // CALCULATING, READY, CONFIRMED, PROCESSING, DONE, FAILED

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @Column(name = "settled_at")
    private LocalDateTime settledAt;

    @Column(name = "bank_name", length = 50)
    private String bankName;

    @Column(name = "account_number", length = 50)
    private String accountNumber;

    @Column(name = "account_holder", length = 50)
    private String accountHolder;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}
