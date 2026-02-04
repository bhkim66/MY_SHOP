package com.my_shop.order.domain;

import com.my_shop.common.entity.BaseEntity;
import com.my_shop.product.domain.Product;
import com.my_shop.product.domain.ProductOption;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "order_items", indexes = {
        @Index(name = "idx_order_item_order", columnList = "order_seq"),
        @Index(name = "idx_order_item_product", columnList = "product_seq"),
        @Index(name = "idx_order_item_status", columnList = "item_status")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq")
    private Long seq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_seq", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_seq", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_option_seq")
    private ProductOption productOption;

    @Column(name = "item_name", nullable = false, length = 200)
    private String itemName;

    @Column(name = "item_option", length = 200)
    private String itemOption;

    @Column(name = "unit_price", nullable = false)
    private int unitPrice;

    @Column(name = "qty", nullable = false)
    private int qty;

    @Column(name = "item_amount", nullable = false)
    private int itemAmount;

    @Column(name = "discount_amount", nullable = false)
    private int discountAmount;

    @Column(name = "item_status", nullable = false, length = 20)
    private String itemStatus;
}
