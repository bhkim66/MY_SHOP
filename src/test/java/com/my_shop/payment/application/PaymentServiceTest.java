package com.my_shop.payment.application;

import com.my_shop.market.domain.entity.Market;
import com.my_shop.member.domain.entity.MemberRole;
import com.my_shop.member.domain.entity.User;
import com.my_shop.order.domain.entity.Order;
import com.my_shop.order.infrastructure.OrderRepository;
import com.my_shop.payment.domain.entity.Payment;
import com.my_shop.payment.infrastructure.PaymentRepository;
import com.my_shop.payment.interfaces.dto.PaymentConfirmResponse;
import com.my_shop.payment.interfaces.dto.PaymentRequest;
import com.my_shop.payment.interfaces.dto.PaymentResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("PaymentService 단위 테스트")
class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private OrderRepository orderRepository;

    private User buyer;
    private Market market;
    private Order pendingOrder;

    @BeforeEach
    void setUp() {
        buyer = User.create("test@test.com", "encoded", "홍길동", "test@test.com", "01012345678", MemberRole.BUYER);
        ReflectionTestUtils.setField(buyer, "seq", 1L);

        market = mock(Market.class);
        lenient().when(market.getSeq()).thenReturn(1L);
        lenient().when(market.getMarketName()).thenReturn("테스트마켓");

        pendingOrder = Order.create(
                "ORD-TEST-0001", market, buyer,
                10000, 3000, 13000,
                "홍길동", "01012345678", "06234", "서울시 강남구", null, null
        );
        ReflectionTestUtils.setField(pendingOrder, "seq", 10L);
    }

    // ========== requestPayment ==========

    @Nested
    @DisplayName("결제 요청")
    class RequestPayment {

        @Test
        @DisplayName("성공: 정상적인 결제 요청을 처리한다")
        void requestPayment_success() {
            // given
            PaymentRequest request = buildPaymentRequest(10L, 13000, "CARD");

            given(orderRepository.findById(10L)).willReturn(Optional.of(pendingOrder));
            given(paymentRepository.findByOrderSeq(10L)).willReturn(Optional.empty());

            Payment savedPayment = Payment.create(pendingOrder, "CARD", 13000, "국민", "1234-****-****-5678", 0);
            ReflectionTestUtils.setField(savedPayment, "seq", 100L);
            given(paymentRepository.save(any(Payment.class))).willReturn(savedPayment);

            // when
            PaymentResponse response = paymentService.requestPayment(request, 1L);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getPayStatus()).isEqualTo("PENDING");
            verify(paymentRepository, times(1)).save(any(Payment.class));
        }

        @Test
        @DisplayName("실패: 결제 금액이 주문 금액과 불일치하면 예외가 발생한다")
        void requestPayment_fail_amount_mismatch() {
            // given
            PaymentRequest request = buildPaymentRequest(10L, 99999, "CARD");

            given(orderRepository.findById(10L)).willReturn(Optional.of(pendingOrder));
            // 금액 불일치 검증이 기존 결제 확인보다 먼저 수행되므로 별도 스터빙 불필요

            // when & then
            assertThatThrownBy(() -> paymentService.requestPayment(request, 1L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("결제 금액이 일치하지 않습니다.");
        }

        @Test
        @DisplayName("실패: 이미 결제가 진행된 주문이면 예외가 발생한다")
        void requestPayment_fail_already_paid() {
            // given
            PaymentRequest request = buildPaymentRequest(10L, 13000, "CARD");

            Payment existingPayment = Payment.create(pendingOrder, "CARD", 13000, null, null, null);
            given(orderRepository.findById(10L)).willReturn(Optional.of(pendingOrder));
            given(paymentRepository.findByOrderSeq(10L)).willReturn(Optional.of(existingPayment));

            // when & then
            assertThatThrownBy(() -> paymentService.requestPayment(request, 1L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("이미 결제가 진행된 주문입니다.");
        }

        @Test
        @DisplayName("실패: 타인의 주문을 결제하려 하면 예외가 발생한다")
        void requestPayment_fail_access_denied() {
            // given
            User anotherBuyer = User.create("other@test.com", "encoded", "김철수", "other@test.com", "01099999999", MemberRole.BUYER);
            ReflectionTestUtils.setField(anotherBuyer, "seq", 99L);

            Order anotherOrder = Order.create(
                    "ORD-OTHER-001", market, anotherBuyer,
                    10000, 3000, 13000,
                    "김철수", "01099999999", "06234", "서울시", null, null
            );
            ReflectionTestUtils.setField(anotherOrder, "seq", 20L);

            PaymentRequest request = buildPaymentRequest(20L, 13000, "CARD");

            given(orderRepository.findById(20L)).willReturn(Optional.of(anotherOrder));

            // when & then
            assertThatThrownBy(() -> paymentService.requestPayment(request, 1L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("본인의 주문만 결제할 수 있습니다.");
        }

        @Test
        @DisplayName("실패: PENDING이 아닌 주문 상태에서 결제 요청 시 예외가 발생한다")
        void requestPayment_fail_invalid_order_status() {
            // given
            pendingOrder.completePayment(); // PAYMENT_COMPLETED 로 변경

            PaymentRequest request = buildPaymentRequest(10L, 13000, "CARD");

            given(orderRepository.findById(10L)).willReturn(Optional.of(pendingOrder));

            // when & then
            assertThatThrownBy(() -> paymentService.requestPayment(request, 1L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("결제 가능한 상태가 아닙니다.");
        }
    }

    // ========== confirmPayment ==========

    @Nested
    @DisplayName("결제 확인")
    class ConfirmPayment {

        @Test
        @DisplayName("성공: 결제를 승인하고 주문 상태가 PAYMENT_COMPLETED로 변경된다")
        void confirmPayment_success() {
            // given
            Payment pendingPayment = Payment.create(pendingOrder, "CARD", 13000, "국민", "1234-****", 0);
            ReflectionTestUtils.setField(pendingPayment, "seq", 100L);

            given(paymentRepository.findById(100L)).willReturn(Optional.of(pendingPayment));

            // when
            PaymentConfirmResponse response = paymentService.confirmPayment(100L, 1L);

            // then
            assertThat(response.getPayStatus()).isEqualTo("APPROVED");
            assertThat(response.getMessage()).isEqualTo("결제가 완료되었습니다.");
            assertThat(pendingOrder.getOrderStatus()).isEqualTo("PAYMENT_COMPLETED");
            assertThat(pendingPayment.getPgTid()).isNotNull().startsWith("MOCK_");
        }

        @Test
        @DisplayName("실패: 이미 처리된 결제를 재확인하려 하면 예외가 발생한다")
        void confirmPayment_fail_already_processed() {
            // given
            Payment approvedPayment = Payment.create(pendingOrder, "CARD", 13000, null, null, null);
            ReflectionTestUtils.setField(approvedPayment, "seq", 100L);
            approvedPayment.approve("MOCK_PG_TID", "https://mock-receipt.example.com");

            given(paymentRepository.findById(100L)).willReturn(Optional.of(approvedPayment));

            // when & then
            assertThatThrownBy(() -> paymentService.confirmPayment(100L, 1L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("이미 처리된 결제입니다.");
        }

        @Test
        @DisplayName("실패: 타인의 결제를 확인하려 하면 예외가 발생한다")
        void confirmPayment_fail_access_denied() {
            // given
            User anotherBuyer = User.create("other@test.com", "encoded", "김철수", "other@test.com", "01099999999", MemberRole.BUYER);
            ReflectionTestUtils.setField(anotherBuyer, "seq", 99L);

            Order anotherOrder = Order.create(
                    "ORD-OTHER-001", market, anotherBuyer,
                    10000, 3000, 13000,
                    "김철수", "01099999999", "06234", "서울시", null, null
            );

            Payment anotherPayment = Payment.create(anotherOrder, "CARD", 13000, null, null, null);
            ReflectionTestUtils.setField(anotherPayment, "seq", 200L);

            given(paymentRepository.findById(200L)).willReturn(Optional.of(anotherPayment));

            // when & then
            assertThatThrownBy(() -> paymentService.confirmPayment(200L, 1L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("본인의 결제만 확인할 수 있습니다.");
        }

        @Test
        @DisplayName("실패: 존재하지 않는 결제 확인 시 예외가 발생한다")
        void confirmPayment_fail_not_found() {
            // given
            given(paymentRepository.findById(999L)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> paymentService.confirmPayment(999L, 1L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("결제 정보를 찾을 수 없습니다.");
        }
    }

    // ========== 헬퍼 메서드 ==========

    private PaymentRequest buildPaymentRequest(Long orderSeq, Integer payAmount, String payMethod) {
        PaymentRequest request = new PaymentRequest();
        ReflectionTestUtils.setField(request, "orderSeq", orderSeq);
        ReflectionTestUtils.setField(request, "payAmount", payAmount);
        ReflectionTestUtils.setField(request, "payMethod", payMethod);
        return request;
    }
}
