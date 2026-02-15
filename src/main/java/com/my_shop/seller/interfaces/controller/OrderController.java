package com.my_shop.seller.interfaces.controller;

import com.my_shop.seller.application.OrderService;
import com.my_shop.seller.interfaces.dto.RecentOrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        // SecurityContext에서 사용자 ID 추출
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long sellerSeq = Long.parseLong(authentication.getName());

        List<RecentOrderResponse> recentOrders = orderService.getRecentOrders(sellerSeq);
        return ResponseEntity.ok(recentOrders);
    }
}
