package com.my_shop.ai.domain;

import com.my_shop.market.domain.Market;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "ai_market_descriptions", indexes = {
        @Index(name = "idx_ai_desc_market_date", columnList = "market_seq, created_at")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class AiMarketDescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq")
    private Long seq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "market_seq", nullable = false)
    private Market market;

    @Column(name = "prompt", columnDefinition = "TEXT")
    private String prompt;

    @Column(name = "generated_description", nullable = false, columnDefinition = "TEXT")
    private String generatedDescription;

    @Column(name = "ai_model", length = 50)
    private String aiModel;

    @Column(name = "is_applied", nullable = false)
    private boolean isApplied;

    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @CreatedBy
    @Column(name = "created_by", updatable = false, nullable = false)
    private Long createdBy;
}
