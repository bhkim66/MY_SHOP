# MY_SHOP JPA Entity Structure

본 문서는 `MY_SHOP` 프로젝트에 구현된 JPA Entity 클래스들의 구조와 역할을 정리합니다.
테이블 정의서(`TABLE_SCHEMA.dbml`)를 기반으로 작성되었습니다.

## 1. Common (공통)
모든 도메인에서 공통으로 사용하는 기반 엔티티입니다. (util, interceptor, aop 등)
- **`BaseTimeEntity`**: `createdAt`, `updatedAt` 등 시간 정보를 자동 관리합니다.
- **`BaseEntity`** (`BaseTimeEntity` 상속): `createdBy`, `updatedBy` 등 생성/수정자 정보까지 포함하여 전체 감사(Audit) 기능을 제공합니다.

---

## 2. Member (회원)
- **`User`** (`users`): 핵심 사용자 정보 (로그인 ID, 비밀번호, 이름, 상태 등).
- **`RefreshToken`** (`refresh_token`): JWT 리프레시 토큰 저장 및 세션 관리.
- **`UserSocialAccount`** (`user_social_accounts`): 소셜 로그인(Kakao, Google, Naver) 연동 정보.
- **`UserAddress`** (`user_addresses`): 회원의 배송지 목록 관리.

---

## 3. Market (마켓)
- **`Market`** (`markets`): 판매자(Seller)가 운영하는 상점(마켓) 정보.
- **`MarketSetting`** (`market_settings`): 마켓별 개별 설정(Key-Value).

---

## 4. Product (상품)
- **`Category`** (`categories`): 상품 카테고리 (계층형 구조 지원).
- **`Product`** (`products`): 판매 상품의 기본 정보 (가격, 설명, 재고 요약 등).
- **`ProductOption`** (`product_options`): 상품의 옵션 상세 (색상, 사이즈 등) 및 옵션별 재고.
- **`ProductImage`** (`product_images`): 상품 썸네일 및 상세 이미지 목록.

---

## 5. Order (주문)
- **`Cart`** (`carts`): 장바구니에 담긴 상품 정보.
- **`Wishlist`** (`wishlists`): 위시리스트(찜하기) 정보.
- **`Order`** (`orders`): 주문서 헤더 (주문자, 배송 정보, 결제 요약).
- **`OrderItem`** (`order_items`): 주문 상세 품목 (상품, 옵션, 수량, 가격).
- **`OrderStatusHistory`** (`order_status_history`): 주문 상태 변경 이력 로그.

---

## 6. Payment (결제)
- **`Payment`** (`payments`): 결제 시도 및 승인 정보 (PG사 연동 데이터).
- **`Refund`** (`refunds`): 환불 처리 이력.

---

## 7. Delivery (배송)
- **`Shipment`** (`shipments`): 배송 정보 (택배사, 운송장 번호, 배송 상태).

---

## 8. Promotion (프로모션 - 쿠폰)
- **`Coupon`** (`coupons`): 쿠폰 마스터 정보 (할인율, 유효기간 등).
- **`UserCoupon`** (`user_coupons`): 사용자에게 발급된 쿠폰 및 사용 여부.

---

## 9. Interaction (상호작용)
### Review (리뷰)
- **`Review`** (`reviews`): 구매평 본문 및 평점.
- **`ReviewImage`** (`review_images`): 리뷰 첨부 이미지.
- **`ReviewHelpful`** (`review_helpful`): 리뷰 '도움이 돼요' 카운트.

### Inquiry (문의)
- **`Inquiry`** (`inquiries`): 상품 또는 주문 관련 문의글.
- **`InquiryAnswer`** (`inquiry_answers`): 문의에 대한 답변.

### Notification (알림)
- **`Notification`** (`notifications`): 사용자 알림 내역.

---

## 10. Settlement (정산)
- **`Settlement`** (`settlements`): 마켓별 정산 집계 내역.
- **`SettlementOrder`** (`settlement_orders`): 정산에 포함된 개별 주문 상세.
- **`FeePolicy`** (`fee_policies`): 마켓별 수수료 정책 히스토리.

---

## 11. Statistics (통계)
- **`ProductViewLog`** (`product_view_logs`): 상품 조회 로그 (조회수 집계용).
- **`SearchLog`** (`search_logs`): 검색 키워드 로그.

---

## 12. AI Service
- **`AiMarketDescription`** (`ai_market_descriptions`): AI가 생성한 마켓/상품 설명 데이터 및 프롬프트 기록.
