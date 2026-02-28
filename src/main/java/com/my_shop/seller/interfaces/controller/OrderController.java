package com.my_shop.seller.interfaces.controller;

import com.my_shop.seller.application.OrderService;
import com.my_shop.seller.interfaces.dto.OrderDetailResponse;
import com.my_shop.seller.interfaces.dto.OrderListResponse;
import com.my_shop.seller.interfaces.dto.OrderStatusUpdateRequest;
import com.my_shop.seller.interfaces.dto.RecentOrderResponse;
import com.my_shop.seller.interfaces.dto.ShipmentRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * SELLER 주문 관리 컸트롤러
 */
@RestController
@RequestMapping("/v1/seller/orders")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SELLER')")
public class OrderController {

    private final OrderService orderService;

    /**
     * 최근 주문 조회 (최근 10건)
     *
     * @return 최근 주문 목록
     */
    @GetMapping("/recent")
    public ResponseEntity<List<RecentOrderResponse>> getRecentOrders() {
        Long sellerSeq = getSellerSeq();
        List<RecentOrderResponse> recentOrders = orderService.getRecentOrders(sellerSeq);
        return ResponseEntity.ok(recentOrders);
    }

    /**
     * 판매자 주문 목록 조회 (페이징, 필터링)
     *
     * @param status 주문 상태 필터 (선택)
     * @param startDate 시작 날짜 (선택)
     * @param endDate 종료 날짜 (선택)
     * @param pageable 페이징 정보
     * @return 주문 목록 (페이징)
     */
    @GetMapping
    public ResponseEntity<Page<OrderListResponse>> getOrders(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @PageableDefault(size = 20, sort = "orderedAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Long sellerSeq = getSellerSeq();
        Page<OrderListResponse> orders = orderService.getOrders(sellerSeq, status, startDate, endDate, pageable);
        return ResponseEntity.ok(orders);
    }

    /**
     * 판매자 주문 상세 조회
     *
     * @param orderSeq 주문 ID
     * @return 주문 상세 정보
     */
    @GetMapping("/{orderSeq}")
    public ResponseEntity<OrderDetailResponse> getOrderDetail(@PathVariable Long orderSeq) {
        Long sellerSeq = getSellerSeq();
        OrderDetailResponse orderDetail = orderService.getOrderDetail(sellerSeq, orderSeq);
        return ResponseEntity.ok(orderDetail);
    }

    /**
     * 주문 상태 변경
     *
     * @param orderSeq 주문 ID
     * @param request 상태 변경 요청
     * @return 성공 응답
     */
    @PatchMapping("/{orderSeq}/status")
    public ResponseEntity<Void> updateOrderStatus(
            @PathVariable Long orderSeq,
            @Valid @RequestBody OrderStatusUpdateRequest request) {

        Long sellerSeq = getSellerSeq();
        orderService.updateOrderStatus(
                sellerSeq,
                orderSeq,
                request.getStatus(),
                request.getReason(),
                request.getShippingCompany(),
                request.getTrackingNumber()
        );
        return ResponseEntity.ok().build();
    }

    /**
     * 배송 정보 등록/수정
     *
     * @param orderSeq 주문 ID
     * @param request 배송 정보 요청
     * @return 성공 응답
     */
    @PostMapping("/{orderSeq}/shipment")
    public ResponseEntity<Void> registerOrUpdateShipment(
            @PathVariable Long orderSeq,
            @Valid @RequestBody ShipmentRequest request) {

        Long sellerSeq = getSellerSeq();
        orderService.registerOrUpdateShipment(
                sellerSeq,
                orderSeq,
                request.getShippingCompany(),
                request.getTrackingNumber()
        );
        return ResponseEntity.ok().build();
    }

    /**
     * SecurityContext에서 판매자 ID 추출
     */
    private Long getSellerSeq() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return Long.parseLong(authentication.getName());
    }
}
