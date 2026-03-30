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

    /**
     * 장바구니 항목 생성 정적 팩토리 메서드
     */
    public static Cart create(User user, Product product, ProductOption productOption, int qty) {
        Cart cart = new Cart();
        cart.user = user;
        cart.product = product;
        cart.productOption = productOption;
        cart.qty = qty;
        return cart;
    }

    /**
     * 수량 변경
     */
    public void updateQty(int qty) {
        this.qty = qty;
    }

    /**
     * 수량 증가
     */
    public void addQty(int qty) {
        this.qty += qty;
    }
}
