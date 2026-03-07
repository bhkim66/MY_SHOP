package com.my_shop.product.domain.entity;

import com.my_shop.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product_options", indexes = {
        @Index(name = "idx_option_product_status", columnList = "product_seq, status"),
        @Index(name = "idx_option_group_sort", columnList = "option_group, sort_order")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ProductOption extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq")
    private Long seq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_seq", nullable = false)
    private Product product;

    @Column(name = "option_group", nullable = false, length = 50)
    private String optionGroup;

    @Column(name = "option_name", nullable = false, length = 100)
    private String optionName;

    @Column(name = "option_value", nullable = false, length = 100)
    private String optionValue;

    @Column(name = "additional_price", nullable = false)
    private int additionalPrice;

    @Column(name = "stock_qty", nullable = false)
    private int stockQty;

    @Column(name = "status", nullable = false, length = 20)
    private String status; // ACTIVE, INACTIVE

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;
}
