package com.my_shop.promotion.domain.entity;

import com.my_shop.member.domain.entity.User;
import com.my_shop.order.domain.entity.Order;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_coupons", indexes = {
        @Index(name = "idx_user_coupon_status", columnList = "user_seq, is_used, expires_at"),
        @Index(name = "idx_user_coupon_coupon", columnList = "coupon_seq"),
        @Index(name = "idx_user_coupon_order", columnList = "order_seq")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class UserCoupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq")
    private Long seq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_seq", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_seq", nullable = false)
    private Coupon coupon;

    @Column(name = "is_used", nullable = false)
    private boolean isUsed;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_seq")
    private Order order;

    @CreatedDate
    @Column(name = "issued_at", updatable = false, nullable = false)
    private LocalDateTime issuedAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;
}
