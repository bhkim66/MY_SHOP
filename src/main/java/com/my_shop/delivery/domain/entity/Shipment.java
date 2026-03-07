package com.my_shop.delivery.domain.entity;

import com.my_shop.common.entity.BaseEntity;
import com.my_shop.order.domain.entity.Order;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "shipments", indexes = {
        @Index(name = "idx_shipment_order", columnList = "order_seq"),
        @Index(name = "idx_shipment_tracking", columnList = "tracking_number"),
        @Index(name = "idx_shipment_status", columnList = "shipping_status")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Shipment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq")
    private Long seq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_seq", nullable = false)
    private Order order;

    @Column(name = "shipping_company", length = 50)
    private String shippingCompany;

    @Column(name = "tracking_number", length = 50)
    private String trackingNumber;

    @Column(name = "shipping_status", nullable = false, length = 20)
    private String shippingStatus;

    @Column(name = "shipped_at")
    private LocalDateTime shippedAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    /**
     * 배송 정보 생성 팩토리 메서드
     */
    public static Shipment create(Order order, String shippingCompany, String trackingNumber) {
        return Shipment.builder()
                .order(order)
                .shippingCompany(shippingCompany)
                .trackingNumber(trackingNumber)
                .shippingStatus("PREPARING")
                .build();
    }

    /**
     * 배송 정보 업데이트
     */
    public void updateShippingInfo(String shippingCompany, String trackingNumber) {
        this.shippingCompany = shippingCompany;
        this.trackingNumber = trackingNumber;
    }

    /**
     * 배송 시작
     */
    public void startShipping() {
        this.shippingStatus = "SHIPPING";
        this.shippedAt = LocalDateTime.now();
    }

    /**
     * 배송 완료
     */
    public void completeDelivery() {
        this.shippingStatus = "DELIVERED";
        this.deliveredAt = LocalDateTime.now();
    }
}
