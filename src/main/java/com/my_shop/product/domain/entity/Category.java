package com.my_shop.product.domain.entity;

import com.my_shop.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "categories", indexes = {
        @Index(name = "idx_category_parent_sort", columnList = "parent_seq, sort_order"),
        @Index(name = "idx_category_code", columnList = "category_code", unique = true),
        @Index(name = "idx_category_display", columnList = "is_display")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq")
    private Long seq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_seq")
    private Category parent;

    @Column(name = "category_name", nullable = false, length = 100)
    private String categoryName;

    @Column(name = "category_code", nullable = false, length = 50, unique = true)
    private String categoryCode;

    @Column(name = "depth", nullable = false)
    private int depth;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    @Column(name = "is_display", nullable = false)
    private boolean isDisplay;
}
