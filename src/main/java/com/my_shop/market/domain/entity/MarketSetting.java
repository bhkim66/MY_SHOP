package com.my_shop.market.domain.entity;

import com.my_shop.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "market_settings", indexes = {
        @Index(name = "idx_market_setting_key", columnList = "market_seq, setting_key", unique = true)
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MarketSetting extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq")
    private Long seq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "market_seq", nullable = false)
    private Market market;

    @Column(name = "setting_key", nullable = false, length = 50)
    private String settingKey;

    @Column(name = "setting_value", columnDefinition = "TEXT")
    private String settingValue;
}
