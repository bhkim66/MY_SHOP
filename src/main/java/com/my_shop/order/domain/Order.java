package com.my_shop.order.domain;

import com.my_shop.common.entity.BaseEntity;
import com.my_shop.market.domain.Market;
import com.my_shop.member.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders", indexes = {
        @Index(name = "idx_order_no", columnList = "order_no", unique = true),
        @Index(name = "idx_order_buyer_date", columnList = "buyer_user_seq, ordered_at"),
        @Index(name = "idx_order_market_status", columnList = "market_seq, order_status, ordered_at")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq")
    private Long seq;

    @Column(name = "order_no", nullable = false, length = 50, unique = true)
    private String orderNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "market_seq", nullable = false)
    private Market market;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_user_seq")
    private User buyer;

    @Column(name = "order_status", nullable = false, length = 20)
    private String orderStatus;

    @Column(name = "total_product_amount", nullable = false)
    private int totalProductAmount;

    @Column(name = "total_discount_amount", nullable = false)
    private int totalDiscountAmount;

    @Column(name = "coupon_discount_amount", nullable = false)
    private int couponDiscountAmount;

    @Column(name = "shipping_fee", nullable = false)
    private int shippingFee;

    @Column(name = "total_pay_amount", nullable = false)
    private int totalPayAmount;

    @Column(name = "receiver_name", nullable = false, length = 50)
    private String receiverName;

    @Column(name = "receiver_phone", nullable = false, length = 20)
    private String receiverPhone;

    @Column(name = "zip_code", length = 10)
    private String zipCode;

    @Column(name = "address1", length = 200)
    private String address1;

    @Column(name = "address2", length = 200)
    private String address2;

    @Column(name = "shipping_message", length = 500)
    private String shippingMessage;

    @Column(name = "buyer_name", length = 50)
    private String buyerName;

    @Column(name = "buyer_email", length = 100)
    private String buyerEmail;

    @Column(name = "buyer_phone", length = 20)
    private String buyerPhone;

    @Column(name = "ordered_at", nullable = false)
    private LocalDateTime orderedAt;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @Column(name = "canceled_at")
    private LocalDateTime canceledAt;

    @Column(name = "cancel_reason", length = 500)
    private String cancelReason;
}
