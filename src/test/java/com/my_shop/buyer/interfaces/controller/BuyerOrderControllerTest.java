package com.my_shop.buyer.interfaces.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.my_shop.buyer.application.BuyerOrderService;
import com.my_shop.buyer.interfaces.dto.OrderCreateRequest;
import com.my_shop.common.exception.GlobalExceptionHandler;
import com.my_shop.buyer.interfaces.dto.OrderCreateResponse;
import com.my_shop.buyer.interfaces.dto.OrderDetailResponse;
import com.my_shop.buyer.interfaces.dto.OrderListResponse;
import org.junit.jupiter.api.AfterEach;
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
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("BuyerOrderController MockMvc 테스트")
class BuyerOrderControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private BuyerOrderController buyerOrderController;

    @Mock
    private BuyerOrderService buyerOrderService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private UserDetails buyerUserDetails;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(buyerOrderController)
                .setCustomArgumentResolvers(
                        new PageableHandlerMethodArgumentResolver(),
                        new AuthenticationPrincipalArgumentResolver()
                )
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        // ObjectMapper에 JavaTimeModule 등록 (LocalDateTime 직렬화)
        objectMapper.findAndRegisterModules();

        // SecurityContext에 인증 정보 주입 (username = buyerSeq)
        buyerUserDetails = User.withUsername("1")
                .password("encoded")
                .roles("BUYER")
                .build();

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(buyerUserDetails, null, buyerUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    // ========== POST /v1/orders ==========

    @Nested
    @DisplayName("POST /v1/orders - 주문 생성")
    class CreateOrder {

        @Test
        @DisplayName("성공: 주문 생성 요청이 200 OK와 함께 응답을 반환한다")
        void createOrder_success() throws Exception {
            // given
            OrderCreateRequest request = buildOrderCreateRequest();

            OrderCreateResponse response = OrderCreateResponse.builder()
                    .orderSeq(1L)
                    .orderNo("ORD-TEST-0001")
                    .totalPayAmount(13000)
                    .orderStatus("PENDING")
                    .build();

            given(buyerOrderService.createOrder(any(OrderCreateRequest.class), anyLong()))
                    .willReturn(response);

            // when & then
            mockMvc.perform(post("/v1/orders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                            )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.orderSeq").value(1L))
                    .andExpect(jsonPath("$.orderNo").value("ORD-TEST-0001"))
                    .andExpect(jsonPath("$.orderStatus").value("PENDING"))
                    .andExpect(jsonPath("$.totalPayAmount").value(13000));
        }

        @Test
        @DisplayName("실패: 서비스에서 예외 발생 시 500 응답을 반환한다")
        void createOrder_fail_service_exception() throws Exception {
            // given
            OrderCreateRequest request = buildOrderCreateRequest();

            given(buyerOrderService.createOrder(any(OrderCreateRequest.class), anyLong()))
                    .willThrow(new RuntimeException("재고가 부족합니다"));

            // when & then
            mockMvc.perform(post("/v1/orders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                            )
                    .andExpect(status().is5xxServerError());
        }
    }

    // ========== GET /v1/orders ==========

    @Nested
    @DisplayName("GET /v1/orders - 내 주문 목록 조회")
    class GetMyOrders {

        @Test
        @DisplayName("성공: 주문 목록을 200 OK와 함께 페이지 응답으로 반환한다")
        void getMyOrders_success() throws Exception {
            // given
            OrderListResponse orderResponse = OrderListResponse.builder()
                    .orderSeq(1L)
                    .orderNo("ORD-TEST-0001")
                    .orderStatus("PENDING")
                    .totalPayAmount(13000)
                    .marketName("테스트마켓")
                    .orderedAt(LocalDateTime.now())
                    .firstItemName("테스트 상품")
                    .itemCount(1)
                    .build();

            Page<OrderListResponse> page = new PageImpl<>(
                    List.of(orderResponse), PageRequest.of(0, 10), 1
            );

            given(buyerOrderService.getMyOrders(anyLong(), any()))
                    .willReturn(page);

            // when & then
            mockMvc.perform(get("/v1/orders")
                            )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content[0].orderSeq").value(1L))
                    .andExpect(jsonPath("$.content[0].orderNo").value("ORD-TEST-0001"))
                    .andExpect(jsonPath("$.totalElements").value(1));
        }

        @Test
        @DisplayName("성공: 주문이 없을 경우 빈 목록을 반환한다")
        void getMyOrders_empty() throws Exception {
            // given
            Page<OrderListResponse> emptyPage = new PageImpl<>(
                    List.of(), PageRequest.of(0, 10), 0
            );

            given(buyerOrderService.getMyOrders(anyLong(), any()))
                    .willReturn(emptyPage);

            // when & then
            mockMvc.perform(get("/v1/orders")
                            )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content").isEmpty())
                    .andExpect(jsonPath("$.totalElements").value(0));
        }
    }

    // ========== GET /v1/orders/{orderSeq} ==========

    @Nested
    @DisplayName("GET /v1/orders/{orderSeq} - 주문 상세 조회")
    class GetOrderDetail {

        @Test
        @DisplayName("성공: 주문 상세 정보를 200 OK와 함께 반환한다")
        void getOrderDetail_success() throws Exception {
            // given
            OrderDetailResponse detailResponse = OrderDetailResponse.builder()
                    .orderSeq(1L)
                    .orderNo("ORD-TEST-0001")
                    .orderStatus("PENDING")
                    .totalProductAmount(10000)
                    .shippingFee(3000)
                    .totalPayAmount(13000)
                    .receiverName("홍길동")
                    .receiverPhone("01012345678")
                    .address1("서울시 강남구")
                    .marketName("테스트마켓")
                    .orderedAt(LocalDateTime.now())
                    .items(List.of())
                    .build();

            given(buyerOrderService.getOrderDetail(anyLong(), anyLong()))
                    .willReturn(detailResponse);

            // when & then
            mockMvc.perform(get("/v1/orders/1")
                            )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.orderSeq").value(1L))
                    .andExpect(jsonPath("$.orderNo").value("ORD-TEST-0001"))
                    .andExpect(jsonPath("$.orderStatus").value("PENDING"))
                    .andExpect(jsonPath("$.receiverName").value("홍길동"));
        }

        @Test
        @DisplayName("실패: 타인의 주문 조회 시 서비스 예외가 전파된다")
        void getOrderDetail_fail_access_denied() throws Exception {
            // given
            given(buyerOrderService.getOrderDetail(anyLong(), anyLong()))
                    .willThrow(new RuntimeException("본인의 주문만 조회할 수 있습니다."));

            // when & then
            mockMvc.perform(get("/v1/orders/99")
                            )
                    .andExpect(status().is5xxServerError());
        }
    }

    // ========== POST /v1/orders/{orderSeq}/cancel ==========

    @Nested
    @DisplayName("POST /v1/orders/{orderSeq}/cancel - 주문 취소")
    class CancelOrder {

        @Test
        @DisplayName("성공: 주문 취소 요청이 200 OK를 반환한다")
        void cancelOrder_success() throws Exception {
            // given
            doNothing().when(buyerOrderService).cancelOrder(anyLong(), anyLong(), any());

            Map<String, String> body = Map.of("reason", "단순 변심");

            // when & then
            mockMvc.perform(post("/v1/orders/1/cancel")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(body))
                            )
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("성공: 취소 사유 없이 요청해도 200 OK를 반환한다")
        void cancelOrder_success_without_reason() throws Exception {
            // given
            doNothing().when(buyerOrderService).cancelOrder(anyLong(), anyLong(), isNull());

            // when & then
            mockMvc.perform(post("/v1/orders/1/cancel")
                            .contentType(MediaType.APPLICATION_JSON)
                            )
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("실패: 취소 불가 상태 주문 취소 시 서비스 예외가 전파된다")
        void cancelOrder_fail_not_cancelable() throws Exception {
            // given
            org.mockito.Mockito.doThrow(new RuntimeException("현재 상태에서는 주문을 취소할 수 없습니다."))
                    .when(buyerOrderService).cancelOrder(anyLong(), anyLong(), any());

            Map<String, String> body = Map.of("reason", "취소 요청");

            // when & then
            mockMvc.perform(post("/v1/orders/1/cancel")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(body))
                            )
                    .andExpect(status().is5xxServerError());
        }
    }

    // ========== 헬퍼 메서드 ==========

    private OrderCreateRequest buildOrderCreateRequest() {
        OrderCreateRequest.OrderItemRequest itemRequest = new OrderCreateRequest.OrderItemRequest();
        ReflectionTestUtils.setField(itemRequest, "productSeq", 1L);
        ReflectionTestUtils.setField(itemRequest, "qty", 2);

        OrderCreateRequest request = new OrderCreateRequest();
        ReflectionTestUtils.setField(request, "items", List.of(itemRequest));
        ReflectionTestUtils.setField(request, "receiverName", "홍길동");
        ReflectionTestUtils.setField(request, "receiverPhone", "01012345678");
        ReflectionTestUtils.setField(request, "zipCode", "06234");
        ReflectionTestUtils.setField(request, "address1", "서울시 강남구 테헤란로 1");
        return request;
    }
}
