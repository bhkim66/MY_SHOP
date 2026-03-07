package com.my_shop.order.domain.entity;

import com.my_shop.common.entity.BaseTimeEntity;
import com.my_shop.member.domain.entity.User;
import com.my_shop.product.domain.entity.Product;
import com.my_shop.product.domain.entity.ProductOption;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "carts", indexes = {
        @Index(name = "idx_cart_user_product", columnList = "user_seq, product_seq"),
        @Index(name = "idx_cart_session", columnList = "session_id")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cart extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq")
    private Long seq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_seq")
    private User user;

    @Column(name = "session_id", length = 100)
    private String sessionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_seq", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_option_seq")
    private ProductOption productOption;

    @Column(name = "qty", nullable = false)
    private int qty;
}
