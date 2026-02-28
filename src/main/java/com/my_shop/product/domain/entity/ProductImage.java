package com.my_shop.product.domain.entity;

import com.my_shop.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "product_images", indexes = {
        @Index(name = "idx_image_product_type_sort", columnList = "product_seq, image_type, sort_order")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@EntityListeners(AuditingEntityListener.class)
public class ProductImage extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq")
    private Long seq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_seq", nullable = false)
    private Product product;

    @Column(name = "image_url", nullable = false, length = 500)
    private String imageUrl;

    @Column(name = "image_type", nullable = false, length = 20)
    private String imageType; // MAIN, DETAIL, THUMBNAIL

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    @Column(name = "alt_text", length = 200)
    private String altText;

    @CreatedBy
    @Column(name = "created_by", updatable = false, nullable = false)
    private Long createdBy;

    /**
     * ProductImage 생성 정적 팩토리 메서드
     */
    public static ProductImage create(Product product, String imageUrl, String imageType, int sortOrder,
            String altText) {
        return ProductImage.builder()
                .product(product)
                .imageUrl(imageUrl)
                .imageType(imageType)
                .sortOrder(sortOrder)
                .altText(altText)
                .build();
    }
}
