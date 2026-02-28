package com.my_shop.stats.domain.entity;

import com.my_shop.member.domain.entity.User;
import com.my_shop.product.domain.entity.Product;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "product_view_logs", indexes = {
        @Index(name = "idx_view_log_product_date", columnList = "product_seq, viewed_at"),
        @Index(name = "idx_view_log_user_date", columnList = "user_seq, viewed_at"),
        @Index(name = "idx_view_log_session", columnList = "session_id")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductViewLog {
    // Only created, no updated. And specific name viewed_at vs created_at.
    // Schema says VIEWED_AT.

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq")
    private Long seq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_seq", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_seq")
    private User user;

    @Column(name = "session_id", length = 100)
    private String sessionId;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    @Column(name = "viewed_at", nullable = false)
    private LocalDateTime viewedAt;
}
