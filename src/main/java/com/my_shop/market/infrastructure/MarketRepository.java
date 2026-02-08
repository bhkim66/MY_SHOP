package com.my_shop.market.infrastructure;

import com.my_shop.market.domain.entity.Market;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarketRepository extends JpaRepository<Market, Long> {
}
