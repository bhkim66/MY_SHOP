package com.my_shop.buyer.application;

import com.my_shop.buyer.interfaces.dto.CartAddRequest;
import com.my_shop.buyer.interfaces.dto.CartItemResponse;
import com.my_shop.buyer.interfaces.dto.CartResponse;
import com.my_shop.buyer.interfaces.dto.CartUpdateRequest;
import com.my_shop.common.exception.ErrorCode;
import com.my_shop.common.exception.custom.AccessDeniedException;
import com.my_shop.common.exception.custom.EntityNotFoundException;
import com.my_shop.common.exception.custom.InvalidValueException;
import com.my_shop.member.domain.entity.User;
import com.my_shop.member.infrastructure.UserRepository;
import com.my_shop.order.domain.entity.Cart;
import com.my_shop.order.infrastructure.CartRepository;
import com.my_shop.product.domain.entity.Product;
import com.my_shop.product.domain.entity.ProductOption;
import com.my_shop.product.infrastructure.ProductOptionRepository;
import com.my_shop.product.infrastructure.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final ProductOptionRepository productOptionRepository;
    private final UserRepository userRepository;

    /**
     * 장바구니 담기
     * - 상품 존재 및 ON_SALE 상태 확인
     * - 재고 확인 (요청 수량 <= 재고)
     * - 이미 동일 상품+옵션이 담겨있으면 수량 증가 (단, 재고 초과 불가)
     * - 없으면 신규 Cart 생성
     */
    @Transactional
    public CartResponse addToCart(CartAddRequest request, Long userSeq) {
        User user = userRepository.findById(userSeq)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));

        Product product = productRepository.findById(request.getProductSeq())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.PRODUCT_NOT_FOUND));

        if (!"ON_SALE".equals(product.getStatus())) {
            throw new InvalidValueException("현재 판매 중인 상품이 아닙니다: " + product.getProductName());
        }

        // 옵션 처리
        ProductOption productOption = null;
        int availableStock;

        if (request.getProductOptionSeq() != null) {
            productOption = productOptionRepository.findById(request.getProductOptionSeq())
                    .orElseThrow(() -> new EntityNotFoundException("상품 옵션", request.getProductOptionSeq()));

            if (!"ACTIVE".equals(productOption.getStatus())) {
                throw new InvalidValueException("선택할 수 없는 상품 옵션입니다.");
            }
            availableStock = productOption.getStockQty();
        } else {
            availableStock = product.getStockQty();
        }

        // 기존 장바구니 항목 확인 (동일 상품+옵션)
        Optional<Cart> existingCart = findExistingCart(userSeq, request.getProductSeq(), request.getProductOptionSeq());

        if (existingCart.isPresent()) {
            Cart cart = existingCart.get();
            int newQty = cart.getQty() + request.getQty();
            if (newQty > availableStock) {
                throw new InvalidValueException(ErrorCode.INSUFFICIENT_STOCK,
                        "재고가 부족합니다. 현재 재고: " + availableStock + "개, 장바구니 수량: " + cart.getQty() + "개");
            }
            cart.addQty(request.getQty());
        } else {
            if (request.getQty() > availableStock) {
                throw new InvalidValueException(ErrorCode.INSUFFICIENT_STOCK,
                        "재고가 부족합니다. 현재 재고: " + availableStock + "개");
            }
            Cart newCart = Cart.create(user, product, productOption, request.getQty());
            cartRepository.save(newCart);
        }

        return getCart(userSeq);
    }

    /**
     * 장바구니 조회
     */
    @Transactional(readOnly = true)
    public CartResponse getCart(Long userSeq) {
        List<Cart> carts = cartRepository.findByUserSeqOrderByCreatedAtDesc(userSeq);
        List<CartItemResponse> items = carts.stream()
                .map(CartItemResponse::from)
                .collect(Collectors.toList());
        return CartResponse.of(items);
    }

    /**
     * 수량 변경
     * - 본인 장바구니 항목인지 확인
     * - 재고 초과 불가
     */
    @Transactional
    public CartResponse updateCartItem(Long cartSeq, CartUpdateRequest request, Long userSeq) {
        Cart cart = cartRepository.findById(cartSeq)
                .orElseThrow(() -> new EntityNotFoundException("장바구니 항목", cartSeq));

        if (!cart.getUser().getSeq().equals(userSeq)) {
            throw new AccessDeniedException("장바구니");
        }

        // 재고 확인
        int availableStock;
        if (cart.getProductOption() != null) {
            availableStock = cart.getProductOption().getStockQty();
        } else {
            availableStock = cart.getProduct().getStockQty();
        }

        if (request.getQty() > availableStock) {
            throw new InvalidValueException(ErrorCode.INSUFFICIENT_STOCK,
                    "재고가 부족합니다. 현재 재고: " + availableStock + "개");
        }

        cart.updateQty(request.getQty());
        return getCart(userSeq);
    }

    /**
     * 항목 삭제
     * - 본인 장바구니 항목인지 확인
     */
    @Transactional
    public void removeCartItem(Long cartSeq, Long userSeq) {
        Cart cart = cartRepository.findById(cartSeq)
                .orElseThrow(() -> new EntityNotFoundException("장바구니 항목", cartSeq));

        if (!cart.getUser().getSeq().equals(userSeq)) {
            throw new AccessDeniedException("장바구니");
        }

        cartRepository.delete(cart);
    }

    /**
     * 전체 비우기
     */
    @Transactional
    public void clearCart(Long userSeq) {
        cartRepository.deleteByUserSeq(userSeq);
    }

    /**
     * 장바구니 개수 조회 (헤더 뱃지용)
     */
    @Transactional(readOnly = true)
    public long getCartCount(Long userSeq) {
        return cartRepository.countByUserSeq(userSeq);
    }

    // ==================== private methods ====================

    /**
     * 동일 상품+옵션 장바구니 항목 조회
     */
    private Optional<Cart> findExistingCart(Long userSeq, Long productSeq, Long productOptionSeq) {
        if (productOptionSeq != null) {
            return cartRepository.findByUserSeqAndProductSeqAndProductOptionSeq(userSeq, productSeq, productOptionSeq);
        }
        return cartRepository.findByUserSeqAndProductSeqAndProductOptionIsNull(userSeq, productSeq);
    }
}
