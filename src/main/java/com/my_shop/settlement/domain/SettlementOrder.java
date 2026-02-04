package com.my_shop.settlement.domain;

import com.my_shop.common.entity.BaseEntity;
import com.my_shop.order.domain.Order;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "settlement_orders", indexes = {
        @Index(name = "idx_settlement_order_settlement", columnList = "settlement_seq"),
        @Index(name = "idx_settlement_order_order", columnList = "order_seq")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SettlementOrder extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq")
    private Long seq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "settlement_seq", nullable = false)
    private Settlement settlement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_seq", nullable = false)
    private Order order;

    @Column(name = "pay_amount", nullable = false)
    private int payAmount;

    @Column(name = "refund_amount", nullable = false)
    private int refundAmount;

    @Column(name = "fee_amount", nullable = false)
    private int feeAmount;

    @Column(name = "net_amount", nullable = false)
    private int netAmount;
}
