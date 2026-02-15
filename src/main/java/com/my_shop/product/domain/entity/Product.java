package com.my_shop.product.domain.entity;

import com.my_shop.common.entity.BaseEntity;
import com.my_shop.market.domain.entity.Market;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products", indexes = {
        @Index(name = "idx_product_market_status", columnList = "market_seq, status"),
        @Index(name = "idx_product_category_status", columnList = "category_seq, status"),
        @Index(name = "idx_product_code", columnList = "product_code"),
        @Index(name = "idx_product_featured", columnList = "is_featured, status"),
        @Index(name = "idx_product_created_at", columnList = "created_at"),
        @Index(name = "idx_product_view_count", columnList = "view_count"),
        @Index(name = "idx_product_sale_count", columnList = "sale_count")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq")
    private Long seq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "market_seq", nullable = false)
    private Market market;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_seq")
    private Category category;

    @Column(name = "product_name", nullable = false, length = 200)
    private String productName;

    @Column(name = "product_code", length = 50)
    private String productCode;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "price", nullable = false)
    private int price;

    @Column(name = "sale_price")
    private Integer salePrice;

    @Column(name = "stock_qty", nullable = false)
    private int stockQty;

    @Column(name = "min_order_qty", nullable = false)
    private int minOrderQty;

    @Column(name = "max_order_qty")
    private Integer maxOrderQty;

    @Column(name = "status", nullable = false, length = 20)
    private String status; // ON_SALE, SOLD_OUT, HIDDEN, DISCONTINUED

    @Column(name = "thumbnail_url", length = 500)
    private String thumbnailUrl;

    @Column(name = "view_count", nullable = false)
    private int viewCount;

    @Column(name = "sale_count", nullable = false)
    private int saleCount;

    @Column(name = "rating_avg", precision = 3, scale = 2)
    private BigDecimal ratingAvg;

    @Column(name = "review_count", nullable = false)
    private int reviewCount;

    @Column(name = "is_featured", nullable = false)
    private boolean isFeatured;

    @Column(name = "sale_start_at")
    private LocalDateTime saleStartAt;

    @Column(name = "sale_end_at")
    private LocalDateTime saleEndAt;

    /**
     * Product 생성 정적 팩토리 메서드
     */
    public static Product create(Market market, Category category, String productName,
            String description, Integer price, Integer stockQty, String thumbnailUrl) {
        return Product.builder()
                .market(market)
                .category(category)
                .productName(productName)
                .description(description)
                .price(price)
                .stockQty(stockQty)
                .status(stockQty > 0 ? "ON_SALE" : "SOLD_OUT")
                .thumbnailUrl(thumbnailUrl)
                .viewCount(0)
                .saleCount(0)
                .minOrderQty(1) // 기본값
                .build();
    }

    /**
     * 상품 정보 업데이트
     * null인 필드는 업데이트하지 않음
     */
    public void update(String productName, String description, Integer price, Integer stockQty, String thumbnailUrl) {
        if (productName != null) {
            this.productName = productName;
        }
        if (description != null) {
            this.description = description;
        }
        if (price != null) {
            this.price = price;
        }
        if (stockQty != null) {
            this.stockQty = stockQty;
            updateStatus();
        }
        if (thumbnailUrl != null) {
            this.thumbnailUrl = thumbnailUrl;
        }
    }

    /**
     * 재고에 따라 상태 자동 업데이트
     */
    private void updateStatus() {
        if (this.stockQty <= 0) {
            this.status = "SOLD_OUT";
        } else if ("SOLD_OUT".equals(this.status)) {
            this.status = "ON_SALE";
        }
    }

    /**
     * 조회수 증가
     */
    public void increaseViewCount() {
        this.viewCount++;
    }

    /**
     * 재고 감소 (주문 시)
     */
    public void decreaseStock(int quantity) {
        if (this.stockQty < quantity) {
            throw new RuntimeException("재고가 부족합니다");
        }
        this.stockQty -= quantity;
        updateStatus();
    }

    /**
     * 판매 수량 증가 (주문 완료 시)
     */
    public void increaseSaleCount(int quantity) {
        this.saleCount += quantity;
    }
}
