package com.my_shop.payment.application;

import com.my_shop.order.domain.entity.Order;
import com.my_shop.order.infrastructure.OrderRepository;
import com.my_shop.payment.domain.entity.Payment;
import com.my_shop.payment.infrastructure.PaymentRepository;
import com.my_shop.payment.interfaces.dto.PaymentConfirmResponse;
import com.my_shop.payment.interfaces.dto.PaymentRequest;
import com.my_shop.payment.interfaces.dto.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    /**
     * 결제 요청 (Mock)
     */
    @Transactional
    public PaymentResponse requestPayment(PaymentRequest request, Long buyerSeq) {
        // 1. 주문 조회
        Order order = orderRepository.findById(request.getOrderSeq())
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다."));

        // 2. 본인 주문인지 확인
        if (!order.getBuyer().getSeq().equals(buyerSeq)) {
            throw new RuntimeException("본인의 주문만 결제할 수 있습니다.");
        }

        // 3. 주문 상태 확인
        if (!"PENDING".equals(order.getOrderStatus())) {
            throw new RuntimeException("결제 가능한 상태가 아닙니다. 현재 상태: " + order.getOrderStatus());
        }

        // 4. 금액 검증
        if (!request.getPayAmount().equals(order.getTotalPayAmount())) {
            throw new RuntimeException("결제 금액이 일치하지 않습니다. 예상: " + order.getTotalPayAmount() + ", 요청: " + request.getPayAmount());
        }

        // 5. 기존 결제 확인
        if (paymentRepository.findByOrderSeq(order.getSeq()).isPresent()) {
            throw new RuntimeException("이미 결제가 진행된 주문입니다.");
        }

        // 6. 결제 생성
        Payment payment = Payment.create(
                order,
                request.getPayMethod(),
                request.getPayAmount(),
                request.getCardCompany(),
                request.getCardNumber(),
                request.getInstallmentMonth()
        );

        Payment savedPayment = paymentRepository.save(payment);

        return PaymentResponse.from(savedPayment);
    }

    /**
     * 결제 확인 (Mock - 항상 성공)
     */
    @Transactional
    public PaymentConfirmResponse confirmPayment(Long paymentSeq, Long buyerSeq) {
        // 1. 결제 조회
        Payment payment = paymentRepository.findById(paymentSeq)
                .orElseThrow(() -> new RuntimeException("결제 정보를 찾을 수 없습니다."));

        Order order = payment.getOrder();

        // 2. 본인 주문인지 확인
        if (!order.getBuyer().getSeq().equals(buyerSeq)) {
            throw new RuntimeException("본인의 결제만 확인할 수 있습니다.");
        }

        // 3. 결제 상태 확인
        if (!"PENDING".equals(payment.getPayStatus())) {
            throw new RuntimeException("이미 처리된 결제입니다. 현재 상태: " + payment.getPayStatus());
        }

        // 4. Mock 결제 승인 (항상 성공)
        String mockPgTid = "MOCK_" + UUID.randomUUID().toString().replace("-", "").substring(0, 20);
        String mockReceiptUrl = "https://mock-pg.example.com/receipt/" + mockPgTid;

        payment.approve(mockPgTid, mockReceiptUrl);

        // 5. 주문 상태 업데이트
        order.completePayment();

        return PaymentConfirmResponse.builder()
                .paymentSeq(payment.getSeq())
                .orderSeq(order.getSeq())
                .orderNo(order.getOrderNo())
                .payStatus(payment.getPayStatus())
                .payAmount(payment.getPayAmount())
                .approvedAt(payment.getApprovedAt())
                .message("결제가 완료되었습니다.")
                .build();
    }
}
