package com.my_shop.buyer.interfaces.controller;

import com.my_shop.buyer.application.BuyerProductService;
import com.my_shop.buyer.interfaces.dto.BuyerProductDetailResponse;
import com.my_shop.buyer.interfaces.dto.BuyerProductListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/products")
@RequiredArgsConstructor
public class BuyerProductController {

    private final BuyerProductService buyerProductService;

    /**
     * 상품 목록 조회 (공개 API)
     */
    @GetMapping
    public ResponseEntity<Page<BuyerProductListResponse>> getProducts(
            @PageableDefault(size = 12, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(buyerProductService.getProducts(pageable));
    }

    /**
     * 상품 상세 조회 (공개 API)
     */
    @GetMapping("/{seq}")
    public ResponseEntity<BuyerProductDetailResponse> getProductDetail(@PathVariable Long seq) {
        return ResponseEntity.ok(buyerProductService.getProductDetail(seq));
    }
}
