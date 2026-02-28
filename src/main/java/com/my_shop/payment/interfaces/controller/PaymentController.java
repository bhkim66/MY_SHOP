package com.my_shop.payment.interfaces.controller;

import com.my_shop.payment.application.PaymentService;
import com.my_shop.payment.interfaces.dto.PaymentConfirmResponse;
import com.my_shop.payment.interfaces.dto.PaymentRequest;
import com.my_shop.payment.interfaces.dto.PaymentResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/payments")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER') or hasRole('BUYER') or hasRole('SELLER') or hasRole('ADMIN')")
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * 결제 요청
     */
    @PostMapping
    public ResponseEntity<PaymentResponse> requestPayment(
            @RequestBody @Valid PaymentRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long buyerSeq = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.ok(paymentService.requestPayment(request, buyerSeq));
    }

    /**
     * 결제 확인 (승인)
     */
    @PostMapping("/{paymentSeq}/confirm")
    public ResponseEntity<PaymentConfirmResponse> confirmPayment(
            @PathVariable Long paymentSeq,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long buyerSeq = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.ok(paymentService.confirmPayment(paymentSeq, buyerSeq));
    }
}
