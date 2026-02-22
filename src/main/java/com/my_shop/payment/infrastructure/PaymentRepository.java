package com.my_shop.payment.infrastructure;

import com.my_shop.payment.domain.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    /**
     * 주문 ID로 결제 조회
     */
    Optional<Payment> findByOrderSeq(Long orderSeq);

    /**
     * PG 거래 ID로 결제 조회
     */
    Optional<Payment> findByPgTid(String pgTid);
}
