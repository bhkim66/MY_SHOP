package com.my_shop.buyer.application;

import com.my_shop.buyer.interfaces.dto.BuyerProductDetailResponse;
import com.my_shop.buyer.interfaces.dto.BuyerProductListResponse;
import com.my_shop.product.domain.entity.Product;
import com.my_shop.product.domain.entity.ProductImage;
import com.my_shop.product.infrastructure.ProductImageRepository;
import com.my_shop.product.infrastructure.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BuyerProductService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;

    /**
     * 판매중인 상품 목록 조회 (페이징)
     */
    public Page<BuyerProductListResponse> getProducts(Pageable pageable) {
        Page<Product> products = productRepository.findByStatus("ON_SALE", pageable);
        return products.map(BuyerProductListResponse::from);
    }

    /**
     * 상품 상세 조회
     */
    @Transactional
    public BuyerProductDetailResponse getProductDetail(Long productSeq) {
        Product product = productRepository.findById(productSeq)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 상품입니다."));

        // 판매중이 아닌 상품은 조회 불가
        if (!"ON_SALE".equals(product.getStatus())) {
            throw new RuntimeException("현재 판매중인 상품이 아닙니다.");
        }

        // 조회수 증가
        product.increaseViewCount();

        List<ProductImage> images = productImageRepository.findByProductOrderBySortOrderAsc(product);
        return BuyerProductDetailResponse.of(product, images);
    }
}
