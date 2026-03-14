package com.my_shop.product.application;

import com.my_shop.market.domain.entity.Market;
import com.my_shop.market.infrastructure.MarketRepository;
import com.my_shop.product.domain.entity.Category;
import com.my_shop.product.domain.entity.Product;
import com.my_shop.product.domain.entity.ProductImage;
import com.my_shop.product.domain.entity.ProductOption;
import com.my_shop.product.infrastructure.CategoryRepository;
import com.my_shop.product.infrastructure.ProductOptionRepository;
import com.my_shop.product.infrastructure.ProductImageRepository;
import com.my_shop.product.infrastructure.ProductRepository;
import com.my_shop.seller.interfaces.dto.ProductCreateRequest;
import com.my_shop.seller.interfaces.dto.ProductResponse;
import com.my_shop.seller.interfaces.dto.ProductSearchRequest;
import com.my_shop.seller.interfaces.dto.ProductUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductImageRepository productImageRepository;
    private final MarketRepository marketRepository;
    private final ProductOptionRepository productOptionRepository;

    /**
     * 상품 등록 (SELLER 전용)
     */
    @Transactional
    public ProductResponse createProduct(ProductCreateRequest request, Long sellerSeq) {
        // 1. SELLER의 Market 조회
        Market market = marketRepository.findByOwnerSeq(sellerSeq)
                .orElseThrow(() -> new RuntimeException("판매자의 마켓 정보가 없습니다."));

        // 2. Category 조회
        Category category = categoryRepository.findByCategoryCode(request.getCategoryCode())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 카테고리입니다."));

        // 3. Product 생성
        Product product = Product.create(
                market,
                category,
                request.getProductName(),
                request.getDescription(),
                request.getPrice(),
                request.getStockQty(),
                null // 썸네일은 별도로 이미지 업로드 API에서 처리
        );

        Product savedProduct = productRepository.save(product);

        // 4. 옵션 저장
        if (request.getOptions() != null && !request.getOptions().isEmpty()) {
            int sortOrder = 0;
            for (ProductCreateRequest.OptionRequest optionReq : request.getOptions()) {
                ProductOption option = ProductOption.create(
                        savedProduct,
                        optionReq.getOptionGroup(),
                        optionReq.getOptionValue(),
                        optionReq.getAdditionalPrice() != null ? optionReq.getAdditionalPrice() : 0,
                        optionReq.getStockQty() != null ? optionReq.getStockQty() : 0,
                        sortOrder++
                );
                productOptionRepository.save(option);
            }
        }

        return ProductResponse.of(savedProduct, List.of());
    }

    /**
     * 상품 수정 (SELLER 전용)
     */
    @Transactional
    public ProductResponse updateProduct(Long productSeq, ProductUpdateRequest request, Long sellerSeq) {
        // 1. 상품 조회
        Product product = productRepository.findById(productSeq)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 상품입니다."));

        // 2. 소유권 검증
        if (!product.getMarket().getOwner().getSeq().equals(sellerSeq)) {
            throw new RuntimeException("본인의 상품만 수정할 수 있습니다.");
        }

        // 3. 수정
        product.update(
                request.getProductName(),
                request.getDescription(),
                request.getPrice(),
                request.getStockQty(),
                null // 썸네일은 별도 API에서 처리
        );

        // 상태 업데이트 (요청 시)
        if (request.getStatus() != null) {
            // 상태를 별도로 업데이트하려면 Product 엔티티에 setStatus 또는 updateStatus 메서드 필요
            // 현재는 stockQty 변경 시 자동으로 상태가 업데이트됨
        }

        List<ProductImage> images = productImageRepository.findByProductOrderBySortOrderAsc(product);
        return ProductResponse.of(product, images);
    }

    /**
     * 상품 삭제 (소프트 삭제 - 상태를 DISCONTINUED로 변경)
     */
    @Transactional
    public void deleteProduct(Long productSeq, Long sellerSeq) {
        // 1. 상품 조회
        Product product = productRepository.findById(productSeq)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 상품입니다."));

        // 2. 소유권 검증
        if (!product.getMarket().getOwner().getSeq().equals(sellerSeq)) {
            throw new RuntimeException("본인의 상품만 삭제할 수 있습니다.");
        }

        // 3. 소프트 삭제 (상태를 HIDDEN 또는 DISCONTINUED로 변경)
        product.update(null, null, null, null, null);
        // 실제로는 상태를 변경하는 메서드가 필요하지만, 현재는 재고를 0으로 만들어서 SOLD_OUT으로 처리
        product.update(null, null, null, 0, null);
    }

    /**
     * 내 상품 단건 조회 (SELLER 전용)
     */
    @Transactional(readOnly = true)
    public ProductResponse getMyProduct(Long productSeq, Long sellerSeq) {
        Product product = productRepository.findById(productSeq)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 상품입니다."));

        // 소유권 검증
        if (!product.getMarket().getOwner().getSeq().equals(sellerSeq)) {
            throw new RuntimeException("본인의 상품만 조회할 수 있습니다.");
        }

        List<ProductImage> images = productImageRepository.findByProductOrderBySortOrderAsc(product);
        return ProductResponse.of(product, images);
    }

    /**
     * 내 상품 목록 조회 (SELLER 전용, 페이징)
     *
     * @param condition
     */
    @Transactional(readOnly = true)
    public Page<ProductResponse> getMyProducts(Long sellerSeq, Pageable pageable, ProductSearchRequest condition) {
        // SELLER의 Market 조회
        Market market = marketRepository.findByOwnerSeq(sellerSeq)
                .orElseThrow(() -> new RuntimeException("판매자의 마켓 정보가 없습니다."));

        // 검색 조건 파싱
        String productName = (condition != null) ? condition.getProductName() : null;
        String status = (condition != null) ? condition.getStatus() : null;
        Long categorySeq = null;
        if (condition != null && condition.getCategorySeq() != null && !condition.getCategorySeq().isEmpty()) {
            categorySeq = Long.parseLong(condition.getCategorySeq());
        }

        // Market의 상품 목록 조회
        Page<Product> productsPage = productRepository.searchByCondition(market, productName, status, categorySeq, pageable);

        // ProductResponse로 변환
        return productsPage.map(product -> {
            List<ProductImage> images = productImageRepository.findByProductOrderBySortOrderAsc(product);
            return ProductResponse.of(product, images);
        });
    }

    /**
     * 상품에 이미지 추가
     */
    @Transactional
    public void addProductImage(Long productSeq, String imageUrl, String imageType, Long sellerSeq) {
        Product product = productRepository.findById(productSeq)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 상품입니다."));

        // 소유권 검증
        if (!product.getMarket().getOwner().getSeq().equals(sellerSeq)) {
            throw new RuntimeException("본인의 상품에만 이미지를 추가할 수 있습니다.");
        }

        // 이미지 순서 계산
        List<ProductImage> existingImages = productImageRepository.findByProductOrderBySortOrderAsc(product);
        int nextSortOrder = existingImages.size();

        // ProductImage 생성 및 저장
        ProductImage productImage = ProductImage.create(
                product,
                imageUrl,
                imageType,
                nextSortOrder,
                product.getProductName() // alt text
        );
        productImageRepository.save(productImage);

        // 첫 번째 MAIN 이미지는 썸네일로 설정
        if ("MAIN".equals(imageType) && product.getThumbnailUrl() == null) {
            product.update(null, null, null, null, imageUrl);
        }
    }
}
