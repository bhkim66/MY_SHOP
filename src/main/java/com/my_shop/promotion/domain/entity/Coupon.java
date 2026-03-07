package com.my_shop.promotion.domain.entity;

import com.my_shop.common.entity.BaseEntity;
import com.my_shop.market.domain.entity.Market;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "coupons", indexes = {
        @Index(name = "idx_coupon_code", columnList = "coupon_code", unique = true),
        @Index(name = "idx_coupon_market_active", columnList = "market_seq, is_active"),
        @Index(name = "idx_coupon_period", columnList = "valid_from, valid_to")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coupon extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq")
    private Long seq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "market_seq")
    private Market market;

    @Column(name = "coupon_code", nullable = false, length = 50, unique = true)
    private String couponCode;

    @Column(name = "coupon_name", nullable = false, length = 100)
    private String couponName;

    @Column(name = "coupon_type", nullable = false, length = 20)
    private String couponType; // PERCENTAGE, FIXED, FREE_SHIPPING

    @Column(name = "discount_value", nullable = false)
    private int discountValue;

    @Column(name = "max_discount_amount")
    private Integer maxDiscountAmount;

    @Column(name = "min_order_amount")
    private Integer minOrderAmount;

    @Column(name = "total_issue_count")
    private Integer totalIssueCount;

    @Column(name = "issued_count", nullable = false)
    private int issuedCount;

    @Column(name = "max_use_per_user")
    private Integer maxUsePerUser;

    @Column(name = "valid_from", nullable = false)
    private LocalDateTime validFrom;

    @Column(name = "valid_to", nullable = false)
    private LocalDateTime validTo;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
}
