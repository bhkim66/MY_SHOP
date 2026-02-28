package com.my_shop.market.infrastructure;

import com.my_shop.market.domain.entity.Market;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MarketRepository extends JpaRepository<Market, Long> {

    /**
     * 소유자(SELLER) ID로 Market 조회
     */
    Optional<Market> findByOwnerSeq(Long ownerSeq);
}
