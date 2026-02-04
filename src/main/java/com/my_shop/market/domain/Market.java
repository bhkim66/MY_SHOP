package com.my_shop.market.domain;

import com.my_shop.common.entity.BaseEntity;
import com.my_shop.member.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "markets", indexes = {
        @Index(name = "idx_market_slug", columnList = "market_slug", unique = true),
        @Index(name = "idx_market_owner", columnList = "owner_user_seq"),
        @Index(name = "idx_market_status", columnList = "status")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Market extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq")
    private Long seq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_user_seq", nullable = false)
    private User owner;

    @Column(name = "market_name", nullable = false, length = 100)
    private String marketName;

    @Column(name = "market_slug", nullable = false, length = 100, unique = true)
    private String marketSlug;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    @Column(name = "banner_url", length = 500)
    private String bannerUrl;

    @Column(name = "business_number", length = 20)
    private String businessNumber;

    @Column(name = "business_name", length = 100)
    private String businessName;

    @Column(name = "ceo_name", length = 50)
    private String ceoName;

    @Column(name = "cs_phone", length = 20)
    private String csPhone;

    @Column(name = "cs_email", length = 100)
    private String csEmail;

    @Column(name = "status", nullable = false, length = 20)
    private String status; // PENDING, ACTIVE, INACTIVE, SUSPENDED

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;
}
