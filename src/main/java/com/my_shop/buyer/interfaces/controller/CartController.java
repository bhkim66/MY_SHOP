package com.my_shop.buyer.interfaces.controller;

import com.my_shop.buyer.application.CartService;
import com.my_shop.buyer.interfaces.dto.CartAddRequest;
import com.my_shop.buyer.interfaces.dto.CartResponse;
import com.my_shop.buyer.interfaces.dto.CartUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/v1/cart")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class CartController {

    private final CartService cartService;

    /**
     * 장바구니 담기
     */
    @PostMapping
    public ResponseEntity<CartResponse> addToCart(
            @RequestBody @Valid CartAddRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userSeq = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.ok(cartService.addToCart(request, userSeq));
    }

    /**
     * 장바구니 조회
     */
    @GetMapping
    public ResponseEntity<CartResponse> getCart(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userSeq = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.ok(cartService.getCart(userSeq));
    }

    /**
     * 장바구니 개수 조회 (헤더 뱃지용)
     */
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getCartCount(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userSeq = Long.parseLong(userDetails.getUsername());
        long count = cartService.getCartCount(userSeq);
        return ResponseEntity.ok(Map.of("count", count));
    }

    /**
     * 수량 변경
     */
    @PatchMapping("/{cartSeq}")
    public ResponseEntity<CartResponse> updateCartItem(
            @PathVariable("cartSeq") Long cartSeq,
            @RequestBody @Valid CartUpdateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userSeq = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.ok(cartService.updateCartItem(cartSeq, request, userSeq));
    }

    /**
     * 항목 삭제
     */
    @DeleteMapping("/{cartSeq}")
    public ResponseEntity<Void> removeCartItem(
            @PathVariable("cartSeq") Long cartSeq,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userSeq = Long.parseLong(userDetails.getUsername());
        cartService.removeCartItem(cartSeq, userSeq);
        return ResponseEntity.noContent().build();
    }

    /**
     * 전체 비우기
     */
    @DeleteMapping
    public ResponseEntity<Void> clearCart(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userSeq = Long.parseLong(userDetails.getUsername());
        cartService.clearCart(userSeq);
        return ResponseEntity.noContent().build();
    }
}
