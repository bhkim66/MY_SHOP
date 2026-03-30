package com.my_shop.buyer.application;

import com.my_shop.buyer.interfaces.dto.OrderCreateRequest;
import com.my_shop.buyer.interfaces.dto.OrderCreateResponse;
import com.my_shop.buyer.interfaces.dto.OrderDetailResponse;
import com.my_shop.buyer.interfaces.dto.OrderListResponse;
import com.my_shop.common.utils.OrderNumberGenerator;
import com.my_shop.delivery.infrastructure.ShipmentRepository;
import com.my_shop.market.domain.entity.Market;
import com.my_shop.member.domain.entity.MemberRole;
import com.my_shop.member.domain.entity.User;
import com.my_shop.member.infrastructure.UserRepository;
import com.my_shop.order.domain.entity.Order;
import com.my_shop.order.domain.entity.OrderItem;
import com.my_shop.order.infrastructure.OrderItemRepository;
import com.my_shop.order.infrastructure.OrderRepository;
import com.my_shop.product.domain.entity.Product;
import com.my_shop.product.infrastructure.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("BuyerOrderService 단위 테스트")
class BuyerOrderServiceTest {

    @InjectMocks
    private BuyerOrderService buyerOrderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderNumberGenerator orderNumberGenerator;

    @Mock
    private ShipmentRepository shipmentRepository;

    private User buyer;
    private Market market;
    private Product product;

    @BeforeEach
    void setUp() {
        buyer = User.create("test@test.com", "encoded", "홍길동", "test@test.com", "01012345678", MemberRole.BUYER);
        ReflectionTestUtils.setField(buyer, "seq", 1L);

        market = mock(Market.class);
        lenient().when(market.getSeq()).thenReturn(1L);
        lenient().when(market.getMarketName()).thenReturn("테스트마켓");

        product = Product.create(market, null, "테스트 상품", "상품 설명", 10000, 100, null);
        ReflectionTestUtils.setField(product, "seq", 1L);
    }

    // ========== createOrder ==========

    @Nested
    @DisplayName("주문 생성")
    class CreateOrder {

        @Test
        @DisplayName("성공: 정상적인 주문을 생성한다")
        void createOrder_success() {
            // given
            OrderCreateRequest request = buildOrderCreateRequest(1L, 2);

            given(userRepository.findById(1L)).willReturn(Optional.of(buyer));
            given(productRepository.findById(1L)).willReturn(Optional.of(product));
            given(orderNumberGenerator.generate()).willReturn("ORD-TEST-0001");

            Order savedOrder = Order.create(
                    "ORD-TEST-0001", market, buyer,
                    20000, 3000, 23000,
                    "홍길동", "01012345678", "06234", "서울시 강남구", null, null
            );
            ReflectionTestUtils.setField(savedOrder, "seq", 1L);

            given(orderRepository.save(any(Order.class))).willReturn(savedOrder);
            given(orderItemRepository.save(any(OrderItem.class))).willReturn(mock(OrderItem.class));

            // when
            OrderCreateResponse response = buyerOrderService.createOrder(request, 1L);

            // then
            assertThat(response.getOrderSeq()).isEqualTo(1L);
            assertThat(response.getOrderNo()).isEqualTo("ORD-TEST-0001");
            assertThat(response.getOrderStatus()).isEqualTo("PENDING");
            verify(orderRepository, times(1)).save(any(Order.class));
            verify(orderItemRepository, times(1)).save(any(OrderItem.class));
        }

        @Test
        @DisplayName("실패: 주문 상품 목록이 비어있으면 예외가 발생한다")
        void createOrder_fail_empty_items() {
            // given
            OrderCreateRequest request = new OrderCreateRequest();
            ReflectionTestUtils.setField(request, "items", List.of());
            ReflectionTestUtils.setField(request, "receiverName", "홍길동");
            ReflectionTestUtils.setField(request, "receiverPhone", "01012345678");
            ReflectionTestUtils.setField(request, "address1", "서울시 강남구");

            given(userRepository.findById(1L)).willReturn(Optional.of(buyer));

            // when & then
            assertThatThrownBy(() -> buyerOrderService.createOrder(request, 1L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("최소 1개 이상의 상품이 필요합니다.");
        }

        @Test
        @DisplayName("실패: 재고가 부족하면 예외가 발생한다")
        void createOrder_fail_insufficient_stock() {
            // given
            Product lowStockProduct = Product.create(market, null, "재고부족 상품", "설명", 5000, 1, null);
            ReflectionTestUtils.setField(lowStockProduct, "seq", 2L);

            OrderCreateRequest request = buildOrderCreateRequest(2L, 5);

            given(userRepository.findById(1L)).willReturn(Optional.of(buyer));
            given(productRepository.findById(2L)).willReturn(Optional.of(lowStockProduct));

            // when & then
            assertThatThrownBy(() -> buyerOrderService.createOrder(request, 1L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("재고가 부족합니다");
        }

        @Test
        @DisplayName("실패: 판매 중이 아닌 상품 주문 시 예외가 발생한다")
        void createOrder_fail_not_on_sale() {
            // given
            // 재고 0으로 생성하면 SOLD_OUT 상태가 됨
            Product soldOutProduct = Product.create(market, null, "품절 상품", "설명", 5000, 0, null);
            ReflectionTestUtils.setField(soldOutProduct, "seq", 3L);

            OrderCreateRequest request = buildOrderCreateRequest(3L, 1);

            given(userRepository.findById(1L)).willReturn(Optional.of(buyer));
            given(productRepository.findById(3L)).willReturn(Optional.of(soldOutProduct));

            // when & then
            assertThatThrownBy(() -> buyerOrderService.createOrder(request, 1L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("현재 판매중인 상품이 아닙니다");
        }
    }

    // ========== getMyOrders ==========

    @Nested
    @DisplayName("내 주문 목록 조회")
    class GetMyOrders {

        @Test
        @DisplayName("성공: 본인의 주문 목록을 페이징하여 반환한다")
        void getMyOrders_success() {
            // given
            Order order1 = buildOrder("ORD-0001");
            Order order2 = buildOrder("ORD-0002");
            List<Order> orders = List.of(order1, order2);
            Page<Order> orderPage = new PageImpl<>(orders, PageRequest.of(0, 10), 2);
            Pageable pageable = PageRequest.of(0, 10);

            given(orderRepository.findByBuyerSeqOrderByOrderedAtDesc(1L, pageable)).willReturn(orderPage);
            given(orderItemRepository.findByOrderSeqIn(anyList())).willReturn(List.of());

            // when
            Page<OrderListResponse> result = buyerOrderService.getMyOrders(1L, pageable);

            // then
            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getTotalElements()).isEqualTo(2);
        }

        @Test
        @DisplayName("성공: 주문이 없는 경우 빈 페이지를 반환한다")
        void getMyOrders_empty() {
            // given
            Pageable pageable = PageRequest.of(0, 10);
            Page<Order> emptyPage = new PageImpl<>(List.of(), pageable, 0);

            given(orderRepository.findByBuyerSeqOrderByOrderedAtDesc(1L, pageable)).willReturn(emptyPage);
            given(orderItemRepository.findByOrderSeqIn(anyList())).willReturn(List.of());

            // when
            Page<OrderListResponse> result = buyerOrderService.getMyOrders(1L, pageable);

            // then
            assertThat(result.getContent()).isEmpty();
            assertThat(result.getTotalElements()).isZero();
        }
    }

    // ========== getOrderDetail ==========

    @Nested
    @DisplayName("주문 상세 조회")
    class GetOrderDetail {

        @Test
        @DisplayName("성공: 본인 주문 상세 정보를 반환한다")
        void getOrderDetail_success() {
            // given
            Order order = buildOrder("ORD-0001");
            ReflectionTestUtils.setField(order, "seq", 10L);

            given(orderRepository.findById(10L)).willReturn(Optional.of(order));
            given(orderItemRepository.findByOrderSeq(10L)).willReturn(List.of());
            given(shipmentRepository.findByOrderSeq(10L)).willReturn(Optional.empty());

            // when
            OrderDetailResponse response = buyerOrderService.getOrderDetail(10L, 1L);

            // then
            assertThat(response.getOrderNo()).isEqualTo("ORD-0001");
            assertThat(response.getOrderStatus()).isEqualTo("PENDING");
        }

        @Test
        @DisplayName("실패: 타인의 주문 조회 시 예외가 발생한다")
        void getOrderDetail_fail_access_denied() {
            // given
            User anotherBuyer = User.create("other@test.com", "encoded", "김철수", "other@test.com", "01099999999", MemberRole.BUYER);
            ReflectionTestUtils.setField(anotherBuyer, "seq", 99L);

            Order order = Order.create(
                    "ORD-0001", market, anotherBuyer,
                    10000, 3000, 13000,
                    "김철수", "01099999999", "06234", "서울시 강남구", null, null
            );
            ReflectionTestUtils.setField(order, "seq", 10L);

            given(orderRepository.findById(10L)).willReturn(Optional.of(order));

            // when & then
            assertThatThrownBy(() -> buyerOrderService.getOrderDetail(10L, 1L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("본인의 주문만 조회할 수 있습니다.");
        }

        @Test
        @DisplayName("실패: 존재하지 않는 주문 조회 시 예외가 발생한다")
        void getOrderDetail_fail_not_found() {
            // given
            given(orderRepository.findById(999L)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> buyerOrderService.getOrderDetail(999L, 1L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("주문을 찾을 수 없습니다.");
        }
    }

    // ========== cancelOrder ==========

    @Nested
    @DisplayName("주문 취소")
    class CancelOrder {

        @Test
        @DisplayName("성공: PENDING 상태 주문을 취소하고 재고가 복구된다")
        void cancelOrder_success_and_stock_restored() {
            // given
            Order order = buildOrder("ORD-0001");
            ReflectionTestUtils.setField(order, "seq", 10L);

            OrderItem orderItem = OrderItem.create(order, product, 3);
            ReflectionTestUtils.setField(orderItem, "seq", 100L);

            int stockBefore = product.getStockQty();

            given(orderRepository.findById(10L)).willReturn(Optional.of(order));
            given(orderItemRepository.findByOrderSeq(10L)).willReturn(List.of(orderItem));

            // when
            buyerOrderService.cancelOrder(10L, 1L, "단순 변심");

            // then
            assertThat(order.getOrderStatus()).isEqualTo("CANCELED");
            assertThat(order.getCancelReason()).isEqualTo("단순 변심");
            assertThat(product.getStockQty()).isEqualTo(stockBefore + 3);
        }

        @Test
        @DisplayName("실패: 타인의 주문 취소 시 예외가 발생한다")
        void cancelOrder_fail_access_denied() {
            // given
            User anotherBuyer = User.create("other@test.com", "encoded", "김철수", "other@test.com", "01099999999", MemberRole.BUYER);
            ReflectionTestUtils.setField(anotherBuyer, "seq", 99L);

            Order order = Order.create(
                    "ORD-0001", market, anotherBuyer,
                    10000, 3000, 13000,
                    "김철수", "01099999999", "06234", "서울시", null, null
            );
            ReflectionTestUtils.setField(order, "seq", 10L);

            given(orderRepository.findById(10L)).willReturn(Optional.of(order));

            // when & then
            assertThatThrownBy(() -> buyerOrderService.cancelOrder(10L, 1L, "취소 사유"))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("본인의 주문만 취소할 수 있습니다.");
        }

        @Test
        @DisplayName("실패: SHIPPING 이후 상태 주문 취소 시 예외가 발생한다")
        void cancelOrder_fail_shipping_status() {
            // given
            Order order = buildOrder("ORD-0001");
            ReflectionTestUtils.setField(order, "seq", 10L);
            order.updateStatus("SHIPPING");

            given(orderRepository.findById(10L)).willReturn(Optional.of(order));

            // when & then
            assertThatThrownBy(() -> buyerOrderService.cancelOrder(10L, 1L, "취소 요청"))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("현재 상태에서는 주문을 취소할 수 없습니다.");
        }

        @Test
        @DisplayName("실패: DELIVERED 상태 주문 취소 시 예외가 발생한다")
        void cancelOrder_fail_delivered_status() {
            // given
            Order order = buildOrder("ORD-0001");
            ReflectionTestUtils.setField(order, "seq", 10L);
            order.updateStatus("DELIVERED");

            given(orderRepository.findById(10L)).willReturn(Optional.of(order));

            // when & then
            assertThatThrownBy(() -> buyerOrderService.cancelOrder(10L, 1L, "취소 요청"))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("현재 상태에서는 주문을 취소할 수 없습니다.");
        }
    }

    // ========== 헬퍼 메서드 ==========

    private Order buildOrder(String orderNo) {
        Order order = Order.create(
                orderNo, market, buyer,
                10000, 3000, 13000,
                "홍길동", "01012345678", "06234", "서울시 강남구", null, null
        );
        return order;
    }

    private OrderCreateRequest buildOrderCreateRequest(Long productSeq, int qty) {
        OrderCreateRequest.OrderItemRequest itemRequest = new OrderCreateRequest.OrderItemRequest();
        ReflectionTestUtils.setField(itemRequest, "productSeq", productSeq);
        ReflectionTestUtils.setField(itemRequest, "qty", qty);

        OrderCreateRequest request = new OrderCreateRequest();
        ReflectionTestUtils.setField(request, "items", List.of(itemRequest));
        ReflectionTestUtils.setField(request, "receiverName", "홍길동");
        ReflectionTestUtils.setField(request, "receiverPhone", "01012345678");
        ReflectionTestUtils.setField(request, "zipCode", "06234");
        ReflectionTestUtils.setField(request, "address1", "서울시 강남구 테헤란로 1");
        return request;
    }
}
