package com.my_shop.order.domain;

import com.my_shop.common.entity.BaseTimeEntity;
import com.my_shop.member.domain.User;
import com.my_shop.product.domain.Product;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "wishlists", indexes = {
        @Index(name = "idx_wishlist_user_product", columnList = "user_seq, product_seq", unique = true),
        @Index(name = "idx_wishlist_product", columnList = "product_seq")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Wishlist extends BaseTimeEntity {
    // Only created_at in schema, but extending BaseTimeEntity gives updatedAt too.
    // Schema says: CREATED_AT timestamp [not null, default: `now()`, note: "생성일"]
    // No updated_at. I should use a generic BaseEntity or just fields if I want to
    // be strict.
    // But BaseTimeEntity has both. I'll stick to BaseTimeEntity for convenience, or
    // CreateOnlyEntity?
    // Let's just use fields here to key strict to schema or define
    // BaseCreateEntity.
    // For simplicity, I'll define fields manually here or ignore updated_at column
    // if it's not in DB table?
    // If I map it, Hibernate expects it.
    // Schema doesn't have updated_at.
    // I will explicitly define createdAt here.

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq")
    private Long seq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_seq", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_seq", nullable = false)
    private Product product;

    // derived from BaseTimeEntity is not suitable if table doesn't have updated_at.
    // So I manually add CreatedDate
    @org.springframework.data.annotation.CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    private java.time.LocalDateTime createdAt;
}
