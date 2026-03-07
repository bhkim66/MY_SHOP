package com.my_shop.inquiry.domain.entity;

import com.my_shop.common.entity.BaseEntity;
import com.my_shop.market.domain.entity.Market;
import com.my_shop.member.domain.entity.User;
import com.my_shop.product.domain.entity.Product;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "inquiries", indexes = {
        @Index(name = "idx_inquiry_product_answered", columnList = "product_seq, is_answered"),
        @Index(name = "idx_inquiry_market_answered", columnList = "market_seq, is_answered"),
        @Index(name = "idx_inquiry_user_date", columnList = "user_seq, created_at")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Inquiry extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq")
    private Long seq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_seq")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "market_seq")
    private Market market;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_seq", nullable = false)
    private User user;

    @Column(name = "inquiry_type", nullable = false, length = 20)
    private String inquiryType; // PRODUCT, ORDER, DELIVERY, REFUND, ETC

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "is_secret", nullable = false)
    private boolean isSecret;

    @Column(name = "is_answered", nullable = false)
    private boolean isAnswered;
}
