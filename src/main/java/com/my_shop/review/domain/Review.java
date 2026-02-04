package com.my_shop.review.domain;

import com.my_shop.common.entity.BaseEntity;
import com.my_shop.member.domain.User;
import com.my_shop.order.domain.OrderItem;
import com.my_shop.product.domain.Product;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reviews", indexes = {
        @Index(name = "idx_review_product_status", columnList = "product_seq, status, created_at"),
        @Index(name = "idx_review_user", columnList = "user_seq, status"),
        @Index(name = "idx_review_order_item", columnList = "order_item_seq"),
        @Index(name = "idx_review_rating", columnList = "rating")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq")
    private Long seq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_seq", nullable = false)
    private Product product;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_seq")
    private OrderItem orderItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_seq", nullable = false)
    private User user;

    @Column(name = "rating", nullable = false)
    private int rating;

    @Column(name = "title", length = 200)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "is_verified_purchase", nullable = false)
    private boolean isVerifiedPurchase;

    @Column(name = "helpful_count", nullable = false)
    private int helpfulCount;

    @Column(name = "status", nullable = false, length = 20)
    private String status; // ACTIVE, HIDDEN, DELETED
}
