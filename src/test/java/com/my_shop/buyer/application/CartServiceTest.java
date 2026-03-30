package com.my_shop.buyer.application;

import com.my_shop.buyer.interfaces.dto.CartAddRequest;
import com.my_shop.buyer.interfaces.dto.CartItemResponse;
import com.my_shop.buyer.interfaces.dto.CartResponse;
import com.my_shop.buyer.interfaces.dto.CartUpdateRequest;
import com.my_shop.common.exception.custom.AccessDeniedException;
import com.my_shop.common.exception.custom.InvalidValueException;
import com.my_shop.market.domain.entity.Market;
import com.my_shop.member.domain.entity.MemberRole;
import com.my_shop.member.domain.entity.User;
import com.my_shop.member.infrastructure.UserRepository;
import com.my_shop.order.domain.entity.Cart;
import com.my_shop.order.infrastructure.CartRepository;
import com.my_shop.product.domain.entity.Product;
import com.my_shop.product.infrastructure.ProductOptionRepository;
import com.my_shop.product.infrastructure.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("CartService 단위 테스트")
class CartServiceTest {

    @InjectMocks
    private CartService cartService;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductOptionRepository productOptionRepository;

    @Mock
    private UserRepository userRepository;

    private User user;
    private Market market;
    private Product product;

    @BeforeEach
    void setUp() {
        user = User.create("test@test.com", "encoded", "홍길동", "test@test.com", "01012345678", MemberRole.BUYER);
        ReflectionTestUtils.setField(user, "seq", 1L);

        market = mock(Market.class);
        lenient().when(market.getSeq()).thenReturn(1L);

        product = Product.create(market, null, "테스트 상품", "상품 설명", 10000, 50, null);
        ReflectionTestUtils.setField(product, "seq", 1L);
    }

    // ========== addToCart ==========

    @Nested
    @DisplayName("장바구니 담기")
    class AddToCart {

        @Test
        @DisplayName("성공: 신규 상품을 장바구니에 담는다")
        void addToCart_success_new_item() {
            // given
            CartAddRequest request = buildCartAddRequest(1L, null, 2);

            given(userRepository.findById(1L)).willReturn(Optional.of(user));
            given(productRepository.findById(1L)).willReturn(Optional.of(product));
            given(cartRepository.findByUserSeqAndProductSeqAndProductOptionIsNull(1L, 1L))
                    .willReturn(Optional.empty());
            given(cartRepository.save(any(Cart.class))).willReturn(mock(Cart.class));
            given(cartRepository.findByUserSeqOrderByCreatedAtDesc(1L)).willReturn(List.of());

            // when
            CartResponse response = cartService.addToCart(request, 1L);

            // then
            assertThat(response).isNotNull();
            verify(cartRepository, times(1)).save(any(Cart.class));
        }

        @Test
        @DisplayName("성공: 이미 담긴 동일 상품은 수량이 합산된다")
        void addToCart_success_merge_existing() {
            // given
            CartAddRequest request = buildCartAddRequest(1L, null, 3);

            Cart existingCart = Cart.create(user, product, null, 5);
            ReflectionTestUtils.setField(existingCart, "seq", 100L);

            given(userRepository.findById(1L)).willReturn(Optional.of(user));
            given(productRepository.findById(1L)).willReturn(Optional.of(product));
            given(cartRepository.findByUserSeqAndProductSeqAndProductOptionIsNull(1L, 1L))
                    .willReturn(Optional.of(existingCart));
            given(cartRepository.findByUserSeqOrderByCreatedAtDesc(1L)).willReturn(List.of(existingCart));

            // when
            cartService.addToCart(request, 1L);

            // then
            assertThat(existingCart.getQty()).isEqualTo(8); // 5 + 3
            verify(cartRepository, never()).save(any(Cart.class)); // 기존 항목 수량 변경이므로 save 호출 안 됨
        }

        @Test
        @DisplayName("실패: 재고를 초과하는 수량 담기 시 예외가 발생한다")
        void addToCart_fail_insufficient_stock() {
            // given
            CartAddRequest request = buildCartAddRequest(1L, null, 100);

            given(userRepository.findById(1L)).willReturn(Optional.of(user));
            given(productRepository.findById(1L)).willReturn(Optional.of(product)); // stock: 50
            given(cartRepository.findByUserSeqAndProductSeqAndProductOptionIsNull(1L, 1L))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> cartService.addToCart(request, 1L))
                    .isInstanceOf(InvalidValueException.class)
                    .hasMessageContaining("재고가 부족합니다.");
        }

        @Test
        @DisplayName("실패: 판매 중지 상품 담기 시 예외가 발생한다")
        void addToCart_fail_not_on_sale() {
            // given
            Product soldOutProduct = Product.create(market, null, "품절 상품", "설명", 5000, 0, null);
            ReflectionTestUtils.setField(soldOutProduct, "seq", 2L);

            CartAddRequest request = buildCartAddRequest(2L, null, 1);

            given(userRepository.findById(1L)).willReturn(Optional.of(user));
            given(productRepository.findById(2L)).willReturn(Optional.of(soldOutProduct));

            // when & then
            assertThatThrownBy(() -> cartService.addToCart(request, 1L))
                    .isInstanceOf(InvalidValueException.class)
                    .hasMessageContaining("현재 판매 중인 상품이 아닙니다");
        }

        @Test
        @DisplayName("실패: 기존 수량 + 추가 수량이 재고를 초과하면 예외가 발생한다")
        void addToCart_fail_exceed_stock_with_existing() {
            // given
            // 재고 50, 기존 담긴 수량 45, 추가 수량 10 -> 합계 55 > 50 초과
            CartAddRequest request = buildCartAddRequest(1L, null, 10);
            Cart existingCart = Cart.create(user, product, null, 45);

            given(userRepository.findById(1L)).willReturn(Optional.of(user));
            given(productRepository.findById(1L)).willReturn(Optional.of(product));
            given(cartRepository.findByUserSeqAndProductSeqAndProductOptionIsNull(1L, 1L))
                    .willReturn(Optional.of(existingCart));

            // when & then
            assertThatThrownBy(() -> cartService.addToCart(request, 1L))
                    .isInstanceOf(InvalidValueException.class)
                    .hasMessageContaining("재고가 부족합니다.");
        }
    }

    // ========== updateCartItem ==========

    @Nested
    @DisplayName("장바구니 수량 변경")
    class UpdateCartItem {

        @Test
        @DisplayName("성공: 장바구니 수량을 정상적으로 변경한다")
        void updateCartItem_success() {
            // given
            Cart cart = Cart.create(user, product, null, 2);
            ReflectionTestUtils.setField(cart, "seq", 100L);

            CartUpdateRequest request = new CartUpdateRequest();
            ReflectionTestUtils.setField(request, "qty", 5);

            given(cartRepository.findById(100L)).willReturn(Optional.of(cart));
            given(cartRepository.findByUserSeqOrderByCreatedAtDesc(1L)).willReturn(List.of(cart));

            // when
            CartResponse response = cartService.updateCartItem(100L, request, 1L);

            // then
            assertThat(cart.getQty()).isEqualTo(5);
            assertThat(response).isNotNull();
        }

        @Test
        @DisplayName("실패: 재고를 초과하는 수량으로 변경 시 예외가 발생한다")
        void updateCartItem_fail_insufficient_stock() {
            // given
            Cart cart = Cart.create(user, product, null, 2);
            ReflectionTestUtils.setField(cart, "seq", 100L);

            CartUpdateRequest request = new CartUpdateRequest();
            ReflectionTestUtils.setField(request, "qty", 999);

            given(cartRepository.findById(100L)).willReturn(Optional.of(cart));

            // when & then
            assertThatThrownBy(() -> cartService.updateCartItem(100L, request, 1L))
                    .isInstanceOf(InvalidValueException.class)
                    .hasMessageContaining("재고가 부족합니다.");
        }

        @Test
        @DisplayName("실패: 타인의 장바구니 수량을 변경하려 하면 예외가 발생한다")
        void updateCartItem_fail_access_denied() {
            // given
            User anotherUser = User.create("other@test.com", "encoded", "김철수", "other@test.com", "01099999999", MemberRole.BUYER);
            ReflectionTestUtils.setField(anotherUser, "seq", 99L);

            Cart anotherCart = Cart.create(anotherUser, product, null, 2);
            ReflectionTestUtils.setField(anotherCart, "seq", 200L);

            CartUpdateRequest request = new CartUpdateRequest();
            ReflectionTestUtils.setField(request, "qty", 3);

            given(cartRepository.findById(200L)).willReturn(Optional.of(anotherCart));

            // when & then
            assertThatThrownBy(() -> cartService.updateCartItem(200L, request, 1L))
                    .isInstanceOf(AccessDeniedException.class);
        }
    }

    // ========== removeCartItem ==========

    @Nested
    @DisplayName("장바구니 항목 삭제")
    class RemoveCartItem {

        @Test
        @DisplayName("성공: 본인의 장바구니 항목을 삭제한다")
        void removeCartItem_success() {
            // given
            Cart cart = Cart.create(user, product, null, 2);
            ReflectionTestUtils.setField(cart, "seq", 100L);

            given(cartRepository.findById(100L)).willReturn(Optional.of(cart));

            // when
            cartService.removeCartItem(100L, 1L);

            // then
            verify(cartRepository, times(1)).delete(cart);
        }

        @Test
        @DisplayName("실패: 타인의 장바구니 항목을 삭제하려 하면 예외가 발생한다")
        void removeCartItem_fail_access_denied() {
            // given
            User anotherUser = User.create("other@test.com", "encoded", "김철수", "other@test.com", "01099999999", MemberRole.BUYER);
            ReflectionTestUtils.setField(anotherUser, "seq", 99L);

            Cart anotherCart = Cart.create(anotherUser, product, null, 2);
            ReflectionTestUtils.setField(anotherCart, "seq", 200L);

            given(cartRepository.findById(200L)).willReturn(Optional.of(anotherCart));

            // when & then
            assertThatThrownBy(() -> cartService.removeCartItem(200L, 1L))
                    .isInstanceOf(AccessDeniedException.class);
            verify(cartRepository, never()).delete(any(Cart.class));
        }

        @Test
        @DisplayName("실패: 존재하지 않는 장바구니 항목 삭제 시 예외가 발생한다")
        void removeCartItem_fail_not_found() {
            // given
            given(cartRepository.findById(999L)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> cartService.removeCartItem(999L, 1L))
                    .isInstanceOf(RuntimeException.class);
        }
    }

    // ========== getCartCount ==========

    @Nested
    @DisplayName("장바구니 개수 조회")
    class GetCartCount {

        @Test
        @DisplayName("성공: 사용자의 장바구니 항목 수를 반환한다")
        void getCartCount_success() {
            // given
            given(cartRepository.countByUserSeq(1L)).willReturn(3L);

            // when
            long count = cartService.getCartCount(1L);

            // then
            assertThat(count).isEqualTo(3L);
        }

        @Test
        @DisplayName("성공: 장바구니가 비어있으면 0을 반환한다")
        void getCartCount_empty() {
            // given
            given(cartRepository.countByUserSeq(1L)).willReturn(0L);

            // when
            long count = cartService.getCartCount(1L);

            // then
            assertThat(count).isZero();
        }
    }

    // ========== 헬퍼 메서드 ==========

    private CartAddRequest buildCartAddRequest(Long productSeq, Long productOptionSeq, int qty) {
        CartAddRequest request = new CartAddRequest();
        ReflectionTestUtils.setField(request, "productSeq", productSeq);
        ReflectionTestUtils.setField(request, "productOptionSeq", productOptionSeq);
        ReflectionTestUtils.setField(request, "qty", qty);
        return request;
    }
}
