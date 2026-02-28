package com.my_shop.product.interfaces.controller;

import com.my_shop.common.service.FileStorageService;
import com.my_shop.product.application.ProductService;
import com.my_shop.product.interfaces.dto.ProductCreateRequest;
import com.my_shop.product.interfaces.dto.ProductResponse;
import com.my_shop.product.interfaces.dto.ProductUpdateRequest;
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
import org.springframework.web.multipart.MultipartFile;

/**
 * 판매자(SELLER) 전용 상품 관리 Controller
 */
@RestController
@RequestMapping("/v1/seller/products")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SELLER')")
public class ProductController {

    private final ProductService productService;
    private final FileStorageService fileStorageService;

    /**
     * 상품 등록
     */
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(
            @RequestBody @Valid ProductCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long sellerSeq = Long.parseLong(userDetails.getUsername());
        ProductResponse response = productService.createProduct(request, sellerSeq);
        return ResponseEntity.ok(response);
    }

    /**
     * 상품 수정
     */
    @PutMapping("/{productSeq}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long productSeq,
            @RequestBody ProductUpdateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long sellerSeq = Long.parseLong(userDetails.getUsername());
        ProductResponse response = productService.updateProduct(productSeq, request, sellerSeq);
        return ResponseEntity.ok(response);
    }

    /**
     * 상품 삭제 (소프트 삭제)
     */
    @DeleteMapping("/{productSeq}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable Long productSeq,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long sellerSeq = Long.parseLong(userDetails.getUsername());
        productService.deleteProduct(productSeq, sellerSeq);
        return ResponseEntity.noContent().build();
    }

    /**
     * 내 상품 단건 조회
     */
    @GetMapping("/{productSeq}")
    public ResponseEntity<ProductResponse> getMyProduct(
            @PathVariable Long productSeq,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long sellerSeq = Long.parseLong(userDetails.getUsername());
        ProductResponse response = productService.getMyProduct(productSeq, sellerSeq);
        return ResponseEntity.ok(response);
    }

    /**
     * 내 상품 목록 조회 (페이징)
     */
    @GetMapping
    public ResponseEntity<Page<ProductResponse>> getMyProducts(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long sellerSeq = Long.parseLong(userDetails.getUsername());
        Page<ProductResponse> response = productService.getMyProducts(sellerSeq, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * 상품 이미지 업로드
     */
    @PostMapping("/{productSeq}/images")
    public ResponseEntity<String> uploadProductImage(
            @PathVariable Long productSeq,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "imageType", defaultValue = "DETAIL") String imageType,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long sellerSeq = Long.parseLong(userDetails.getUsername());

        // 파일 저장
        String imageUrl = fileStorageService.storeFile(file);

        // ProductImage 생성
        productService.addProductImage(productSeq, imageUrl, imageType, sellerSeq);

        return ResponseEntity.ok(imageUrl);
    }
}
