package com.my_shop.delivery.infrastructure;

import com.my_shop.delivery.domain.entity.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Shipment Repository
 */
@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {

    /**
     * 주문별 배송 정보 조회
     */
    Optional<Shipment> findByOrderSeq(Long orderSeq);

    /**
     * 송장번호로 배송 정보 조회
     */
    Optional<Shipment> findByTrackingNumber(String trackingNumber);
}
