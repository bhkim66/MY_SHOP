package com.my_shop.buyer.interfaces.controller;

import com.my_shop.buyer.application.BuyerOrderService;
import com.my_shop.buyer.interfaces.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/v1/orders")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER') or hasRole('BUYER') or hasRole('SELLER') or hasRole('ADMIN')")
public class BuyerOrderController {

    private final BuyerOrderService buyerOrderService;

    /**
     * 주문 생성
     */
    @PostMapping
    public ResponseEntity<OrderCreateResponse> createOrder(
            @RequestBody @Valid OrderCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long buyerSeq = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.ok(buyerOrderService.createOrder(request, buyerSeq));
    }

    /**
     * 내 주문 목록 조회
     */
    @GetMapping
    public ResponseEntity<Page<OrderListResponse>> getMyOrders(
            @PageableDefault(size = 10, sort = "orderedAt", direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long buyerSeq = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.ok(buyerOrderService.getMyOrders(buyerSeq, pageable));
    }

    /**
     * 주문 상세 조회
     */
    @GetMapping("/{orderSeq}")
    public ResponseEntity<OrderDetailResponse> getOrderDetail(
            @PathVariable Long orderSeq,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long buyerSeq = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.ok(buyerOrderService.getOrderDetail(orderSeq, buyerSeq));
    }

    /**
     * 주문 취소
     */
    @PostMapping("/{orderSeq}/cancel")
    public ResponseEntity<Void> cancelOrder(
            @PathVariable Long orderSeq,
            @RequestBody(required = false) Map<String, String> body,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long buyerSeq = Long.parseLong(userDetails.getUsername());
        String reason = body != null ? body.get("reason") : null;
        buyerOrderService.cancelOrder(orderSeq, buyerSeq, reason);
        return ResponseEntity.ok().build();
    }
}
