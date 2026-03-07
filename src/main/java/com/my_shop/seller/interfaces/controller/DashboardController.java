package com.my_shop.seller.interfaces.controller;

import com.my_shop.seller.application.DashboardService;
import com.my_shop.seller.interfaces.dto.DashboardStatsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * SELLER 대시보드 컨트롤러
 */
@RestController
@RequestMapping("/v1/seller/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SELLER')")
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * 대시보드 통계 조회
     *
     * @return 대시보드 통계
     */
    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsResponse> getDashboardStats() {
        // SecurityContext에서 사용자 ID 추출
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long sellerSeq = Long.parseLong(authentication.getName());

        DashboardStatsResponse stats = dashboardService.getDashboardStats(sellerSeq);
        return ResponseEntity.ok(stats);
    }
}
