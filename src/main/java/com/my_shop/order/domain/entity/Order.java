package com.my_shop.order.domain.entity;

import com.my_shop.common.entity.BaseEntity;
import com.my_shop.market.domain.entity.Market;
import com.my_shop.member.domain.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "orders", indexes = {
        @Index(name = "idx_order_no", columnList = "order_no", unique = true),
        @Index(name = "idx_order_buyer_date", columnList = "buyer_user_seq, ordered_at"),
        @Index(name = "idx_order_market_status", columnList = "market_seq, order_status, ordered_at")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Order extends BaseEntity {

    // 취소 가능한 상태 목록
    private static final List<String> CANCELABLE_STATUSES = Arrays.asList(
            "PENDING", "PAYMENT_COMPLETED", "PREPARING"
    );

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

    /**
     * 주문 생성 팩토리 메서드
     */
    public static Order create(String orderNo, Market market, User buyer,
                               int totalProductAmount, int shippingFee, int totalPayAmount,
                               String receiverName, String receiverPhone,
                               String zipCode, String address1, String address2, String shippingMessage) {
        return Order.builder()
                .orderNo(orderNo)
                .market(market)
                .buyer(buyer)
                .orderStatus("PENDING")
                .totalProductAmount(totalProductAmount)
                .totalDiscountAmount(0)
                .couponDiscountAmount(0)
                .shippingFee(shippingFee)
                .totalPayAmount(totalPayAmount)
                .receiverName(receiverName)
                .receiverPhone(receiverPhone)
                .zipCode(zipCode)
                .address1(address1)
                .address2(address2)
                .shippingMessage(shippingMessage)
                .buyerName(buyer.getName())
                .buyerEmail(buyer.getEmail())
                .buyerPhone(buyer.getPhone())
                .orderedAt(LocalDateTime.now())
                .build();
    }

    /**
     * 결제 완료 처리
     */
    public void completePayment() {
        this.orderStatus = "PAYMENT_COMPLETED";
        this.confirmedAt = LocalDateTime.now();
    }

    /**
     * 주문 취소 가능 여부 확인
     */
    public boolean isCancelable() {
        return CANCELABLE_STATUSES.contains(this.orderStatus);
    }

    /**
     * 주문 취소
     */
    public void cancel(String reason) {
        if (!isCancelable()) {
            throw new RuntimeException("현재 상태에서는 주문을 취소할 수 없습니다. 현재 상태: " + this.orderStatus);
        }
        this.orderStatus = "CANCELED";
        this.canceledAt = LocalDateTime.now();
        this.cancelReason = reason;
    }

    /**
     * 상태 변경
     */
    public void updateStatus(String newStatus) {
        this.orderStatus = newStatus;
    }
}
