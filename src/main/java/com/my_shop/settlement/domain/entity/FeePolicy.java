package com.my_shop.settlement.domain.entity;

import com.my_shop.common.entity.BaseEntity;
import com.my_shop.market.domain.entity.Market;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "fee_policies", indexes = {
        @Index(name = "idx_fee_policy_market_active", columnList = "market_seq, is_active, effective_from")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FeePolicy extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq")
    private Long seq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "market_seq")
    private Market market;

    @Column(name = "policy_name", nullable = false, length = 100)
    private String policyName;

    @Column(name = "fee_type", nullable = false, length = 20)
    private String feeType; // PERCENTAGE, FIXED

    @Column(name = "fee_value", nullable = false, precision = 10, scale = 2)
    private BigDecimal feeValue;

    @Column(name = "min_fee")
    private Integer minFee;

    @Column(name = "max_fee")
    private Integer maxFee;

    @Column(name = "effective_from", nullable = false)
    private LocalDateTime effectiveFrom;

    @Column(name = "effective_to")
    private LocalDateTime effectiveTo;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;
}
