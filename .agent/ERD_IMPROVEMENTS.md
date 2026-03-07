# 📊 마켓플레이스 ERD 

### 1️⃣ 사용자 관리 강화
**추가된 테이블:**
- `users` 테이블 개선
  - EMAIL, PHONE 필드 추가 (인증 시스템 대비)
  - STATUS 추가 (ACTIVE, INACTIVE, SUSPENDED, WITHDRAWN)
  - EMAIL_VERIFIED, PHONE_VERIFIED 인증 상태 관리
  - LAST_LOGIN_AT 추가

- `USER_ADDRESSES` (신규)
  - 다중 배송지 관리
  - 기본 배송지 설정
  - 주문 시 배송지 선택 간편화

**인덱스 최적화:**
```sql
(LOGIN_ID) [unique]
(EMAIL) [unique]
(STATUS, ROLE)
(USER_SEQ, IS_DEFAULT) -- USER_ADDRESSES
```

---

### 2️⃣ 마켓 관리 확장
**추가된 테이블:**
- `MARKETS` 테이블 개선
  - LOGO_URL, BANNER_URL (브랜딩)
  - 사업자 정보 (BUSINESS_NUMBER, BUSINESS_NAME, CEO_NAME)
  - 고객센터 정보 (CS_PHONE, CS_EMAIL)
  - STATUS: PENDING, ACTIVE, INACTIVE, SUSPENDED
  - APPROVED_AT (승인 프로세스)

- `MARKET_SETTINGS` (신규)
  - 마켓별 설정 관리 (Key-Value)
  - 배송비 정책, 반품/교환 정책 등
  - JSON 형태로 유연한 확장

- `FEE_POLICIES` (신규)
  - 마켓별 차등 수수료 정책
  - 기간별 수수료 이력 관리
  - 최소/최대 수수료 설정

**인덱스 최적화:**
```sql
(MARKET_SLUG) [unique]
(OWNER_USER_SEQ)
(STATUS)
(MARKET_SEQ, SETTING_KEY) [unique] -- MARKET_SETTINGS
```

---

### 3️⃣ 카테고리 관리 (신규)
**추가된 테이블:**
- `CATEGORIES` (신규)
  - 계층형 카테고리 구조 (PARENT_SEQ 재귀)
  - 무한 depth 지원
  - SORT_ORDER로 순서 관리
  - IS_DISPLAY 노출 제어

**인덱스 최적화:**
```sql
(PARENT_SEQ, SORT_ORDER)
(CATEGORY_CODE) [unique]
(IS_DISPLAY)
```

---

### 4️⃣ 상품 관리 고도화
**추가된 필드:**
- `PRODUCTS` 테이블
  - CATEGORY_SEQ (카테고리 연결)
  - PRODUCT_CODE (상품 코드)
  - PRICE, SALE_PRICE 분리 (할인율 계산)
  - MIN_ORDER_QTY, MAX_ORDER_QTY
  - 통계 필드: VIEW_COUNT, SALE_COUNT, RATING_AVG, REVIEW_COUNT
  - IS_FEATURED (추천 상품)
  - SALE_START_AT, SALE_END_AT (판매 기간)

**추가된 테이블:**
- `PRODUCT_OPTIONS` 개선
  - OPTION_GROUP 추가 (색상, 사이즈 그룹핑)
  - SORT_ORDER (정렬)

- `PRODUCT_IMAGES` (신규)
  - 다중 이미지 지원
  - IMAGE_TYPE (MAIN, DETAIL, THUMBNAIL)
  - SORT_ORDER

**인덱스 최적화:**
```sql
-- PRODUCTS
(MARKET_SEQ, STATUS)
(CATEGORY_SEQ, STATUS)
(PRODUCT_CODE)
(IS_FEATURED, STATUS)
(CREATED_AT)
(VIEW_COUNT)
(SALE_COUNT)

-- PRODUCT_OPTIONS
(PRODUCT_SEQ, STATUS)
(OPTION_GROUP, SORT_ORDER)

-- PRODUCT_IMAGES
(PRODUCT_SEQ, IMAGE_TYPE, SORT_ORDER)
```

---

### 5️⃣ 장바구니 & 위시리스트 (신규)
**추가된 테이블:**
- `CARTS` (신규)
  - 회원/비회원 모두 지원
  - USER_SEQ (회원) 또는 SESSION_ID (비회원)
  - PRODUCT_OPTION_SEQ 지원

- `WISHLISTS` (신규)
  - 찜 기능
  - 사용자별 관심 상품

**인덱스 최적화:**
```sql
-- CARTS
(USER_SEQ, PRODUCT_SEQ)
(SESSION_ID)

-- WISHLISTS
(USER_SEQ, PRODUCT_SEQ) [unique]
(PRODUCT_SEQ)
```

---

### 6️⃣ 주문 시스템 개선
**추가된 필드:**
- `ORDERS` 테이블
  - ORDER_NO 추가 (주문번호)
  - ORDER_STATUS 세분화 (PENDING, CONFIRMED, PREPARING, SHIPPED, DELIVERED, CANCELED, REFUNDED)
  - COUPON_DISCOUNT_AMOUNT 분리
  - SHIPPING_MESSAGE (배송 메시지)
  - 비회원 주문: BUYER_NAME, BUYER_EMAIL, BUYER_PHONE
  - ORDERED_AT, CONFIRMED_AT, CANCELED_AT, CANCEL_REASON

**추가된 테이블:**
- `ORDER_ITEMS` 개선
  - DISCOUNT_AMOUNT (아이템별 할인)
  - ITEM_STATUS (아이템별 상태)

- `ORDER_STATUS_HISTORY` (신규)
  - 주문 상태 변경 이력
  - 감사(Audit) 목적
  - CHANGED_BY, CHANGED_REASON

**인덱스 최적화:**
```sql
-- ORDERS
(ORDER_NO) [unique]
(BUYER_USER_SEQ, ORDERED_AT)
(MARKET_SEQ, ORDER_STATUS, ORDERED_AT)
(ORDER_STATUS, ORDERED_AT)

-- ORDER_ITEMS
(ORDER_SEQ)
(PRODUCT_SEQ)
(ITEM_STATUS)

-- ORDER_STATUS_HISTORY
(ORDER_SEQ, CREATED_AT)
```

---

### 7️⃣ 배송 관리 (신규)
**추가된 테이블:**
- `SHIPMENTS` (신규)
  - 택배사, 운송장 번호
  - SHIPPING_STATUS (PREPARING, SHIPPED, IN_TRANSIT, DELIVERED, RETURNED)
  - SHIPPED_AT, DELIVERED_AT

**인덱스 최적화:**
```sql
(ORDER_SEQ)
(TRACKING_NUMBER)
(SHIPPING_STATUS)
```

---

### 8️⃣ 결제 & 환불 시스템 강화
**추가된 필드:**
- `PAYMENTS` 테이블
  - PAY_STATUS 세분화 (READY, PENDING, SUCCESS, FAILED, CANCELED, REFUNDED)
  - 카드 정보: CARD_COMPANY, CARD_NUMBER, INSTALLMENT_MONTH
  - 가상계좌: VBANK_NAME, VBANK_NUMBER, VBANK_EXPIRES_AT
  - REFUNDED_AT, RECEIPT_URL

**추가된 테이블:**
- `REFUNDS` (신규)
  - 환불 관리 전용 테이블
  - REFUND_STATUS (REQUESTED, APPROVED, PROCESSING, COMPLETED, REJECTED)
  - 환불 계좌 정보
  - PROCESSED_AT, REJECTED_AT, REJECT_REASON

**인덱스 최적화:**
```sql
-- PAYMENTS
(ORDER_SEQ)
(PG_TID)
(PAY_STATUS, APPROVED_AT)

-- REFUNDS
(PAYMENT_SEQ)
(ORDER_SEQ)
(REFUND_STATUS)
```

---

### 9️⃣ 쿠폰 시스템 (신규)
**추가된 테이블:**
- `COUPONS` (신규)
  - COUPON_TYPE (PERCENTAGE, FIXED, FREE_SHIPPING)
  - 할인값, 최대 할인액, 최소 주문액
  - 발행 수량 관리
  - 1인당 사용 제한
  - 유효 기간

- `USER_COUPONS` (신규)
  - 사용자별 쿠폰 발급 이력
  - 사용 여부, 사용 주문 추적

**인덱스 최적화:**
```sql
-- COUPONS
(COUPON_CODE) [unique]
(MARKET_SEQ, IS_ACTIVE)
(VALID_FROM, VALID_TO)

-- USER_COUPONS
(USER_SEQ, IS_USED, EXPIRES_AT)
(COUPON_SEQ)
(ORDER_SEQ)
```

---

### 🔟 리뷰 시스템 (신규)
**추가된 테이블:**
- `REVIEWS` (신규)
  - RATING (1-5)
  - TITLE, CONTENT
  - IS_VERIFIED_PURCHASE (구매 인증)
  - HELPFUL_COUNT (도움됨 카운트)
  - STATUS (ACTIVE, HIDDEN, DELETED)

- `REVIEW_IMAGES` (신규)
  - 리뷰 이미지 첨부

- `REVIEW_HELPFUL` (신규)
  - 리뷰 도움됨 투표

**인덱스 최적화:**
```sql
-- REVIEWS
(PRODUCT_SEQ, STATUS, CREATED_AT)
(USER_SEQ, STATUS)
(ORDER_ITEM_SEQ)
(RATING)

-- REVIEW_HELPFUL
(REVIEW_SEQ, USER_SEQ) [unique]
```

---

### 1️⃣1️⃣ 문의 관리 (신규)
**추가된 테이블:**
- `INQUIRIES` (신규)
  - INQUIRY_TYPE (PRODUCT, ORDER, DELIVERY, REFUND, ETC)
  - IS_SECRET (비밀글)
  - IS_ANSWERED (답변 여부)

- `INQUIRY_ANSWERS` (신규)
  - 문의 답변

**인덱스 최적화:**
```sql
-- INQUIRIES
(PRODUCT_SEQ, IS_ANSWERED)
(MARKET_SEQ, IS_ANSWERED)
(USER_SEQ, CREATED_AT)
```

---

### 1️⃣2️⃣ 정산 관리 개선
**추가된 필드:**
- `SETTLEMENTS` 테이블
  - STATUS 세분화 (CALCULATING, READY, CONFIRMED, PROCESSING, DONE, FAILED)
  - 계좌 정보 (BANK_NAME, ACCOUNT_NUMBER, ACCOUNT_HOLDER)
  - NOTES (비고)

**인덱스 최적화:**
```sql
(MARKET_SEQ, STATUS)
(SETTLEMENT_PERIOD_START, SETTLEMENT_PERIOD_END)
(STATUS, CONFIRMED_AT)
```

---

### 1️⃣3️⃣ 알림 시스템 (신규)
**추가된 테이블:**
- `NOTIFICATIONS` (신규)
  - NOTIFICATION_TYPE (ORDER, PAYMENT, SHIPMENT, REVIEW, INQUIRY, PROMOTION)
  - LINK_URL
  - IS_READ, READ_AT

**인덱스 최적화:**
```sql
(USER_SEQ, IS_READ, CREATED_AT)
(NOTIFICATION_TYPE)
```

---

### 1️⃣4️⃣ 통계 & 로그 (신규)
**추가된 테이블:**
- `PRODUCT_VIEW_LOGS` (신규)
  - 상품 조회 로그
  - 인기 상품 분석, 사용자 행동 분석

- `SEARCH_LOGS` (신규)
  - 검색 로그
  - 인기 검색어, 검색 결과 개선

**인덱스 최적화:**
```sql
-- PRODUCT_VIEW_LOGS
(PRODUCT_SEQ, VIEWED_AT)
(USER_SEQ, VIEWED_AT)
(SESSION_ID)

-- SEARCH_LOGS
(SEARCH_KEYWORD, SEARCHED_AT)
(USER_SEQ, SEARCHED_AT)
```

---

### 1️⃣5️⃣ AI 서비스 (v2)
**추가된 테이블:**
- `AI_MARKET_DESCRIPTIONS` (신규)
  - AI 기반 마켓 설명 생성
  - 프롬프트, 생성 결과 이력
  - IS_APPLIED (적용 여부)

**인덱스 최적화:**
```sql
(MARKET_SEQ, CREATED_AT)
```

---

## 📈 데이터베이스 구조 요약

### 총 테이블 수
- **기존**: 9개
- **개선**: 36개

### 주요 테이블 그룹
1. **사용자 관리** (3개): USER, USER_SOCIAL_ACCOUNTS, USER_ADDRESSES
2. **마켓 관리** (3개): MARKETS, MARKET_SETTINGS, FEE_POLICIES
3. **카테고리** (1개): CATEGORIES
4. **상품 관리** (4개): PRODUCTS, PRODUCT_OPTIONS, PRODUCT_IMAGES, WISHLISTS
5. **장바구니** (1개): CARTS
6. **주문 관리** (4개): ORDERS, ORDER_ITEMS, ORDER_STATUS_HISTORY, SHIPMENTS
7. **결제** (2개): PAYMENTS, REFUNDS
8. **쿠폰** (2개): COUPONS, USER_COUPONS
9. **리뷰** (3개): REVIEWS, REVIEW_IMAGES, REVIEW_HELPFUL
10. **문의** (2개): INQUIRIES, INQUIRY_ANSWERS
11. **정산** (2개): SETTLEMENTS, SETTLEMENT_ORDERS
12. **알림** (1개): NOTIFICATIONS
13. **통계/로그** (2개): PRODUCT_VIEW_LOGS, SEARCH_LOGS
14. **AI** (1개): AI_MARKET_DESCRIPTIONS

---

## 🎯 MVP 단계별 테이블 우선순위

### Phase 1: MVP 핵심 (필수)
```
✅ USER
✅ USER_ADDRESSES
✅ MARKETS
✅ CATEGORIES
✅ PRODUCTS
✅ PRODUCT_OPTIONS
✅ PRODUCT_IMAGES
✅ CARTS
✅ ORDERS
✅ ORDER_ITEMS
✅ PAYMENTS
✅ SETTLEMENTS
✅ SETTLEMENT_ORDERS
```

### Phase 2: 핵심 기능 확장
```
🔄 USER_SOCIAL_ACCOUNTS (OAuth)
🔄 MARKET_SETTINGS
🔄 WISHLISTS
🔄 ORDER_STATUS_HISTORY
🔄 SHIPMENTS
🔄 REFUNDS
🔄 COUPONS
🔄 USER_COUPONS
🔄 NOTIFICATIONS
```

### Phase 3: 고급 기능
```
📊 REVIEWS, REVIEW_IMAGES, REVIEW_HELPFUL
📊 INQUIRIES, INQUIRY_ANSWERS
📊 FEE_POLICIES
📊 PRODUCT_VIEW_LOGS
📊 SEARCH_LOGS
🤖 AI_MARKET_DESCRIPTIONS
```

---

## 🔍 인덱스 전략 핵심

### 1. 복합 인덱스 우선
```sql
-- 자주 함께 사용되는 컬럼
(MARKET_SEQ, STATUS)
(CATEGORY_SEQ, STATUS)
(USER_SEQ, CREATED_AT)
```

### 2. WHERE + ORDER BY 최적화
```sql
-- WHERE 조건 + 정렬 컬럼
(BUYER_USER_SEQ, ORDERED_AT)
(MARKET_SEQ, ORDER_STATUS, ORDERED_AT)
```

### 3. UNIQUE 제약조건
```sql
-- 중복 방지 + 성능
(LOGIN_ID) [unique]
(EMAIL) [unique]
(ORDER_NO) [unique]
(COUPON_CODE) [unique]
```

---

## 💡 슬로우 쿼리 방지 전략

### 1. 인덱스 활용 가능한 쿼리 작성
```sql
-- ✅ Good: 인덱스 활용
SELECT * FROM PRODUCTS 
WHERE MARKET_SEQ = 1 AND STATUS = 'ON_SALE'
ORDER BY CREATED_AT DESC;

-- ❌ Bad: 인덱스 활용 불가
SELECT * FROM PRODUCTS 
WHERE CONCAT(MARKET_SEQ, STATUS) = '1ON_SALE';
```

### 2. LIKE 검색 최소화
```sql
-- ⚠️ 성능 주의: Full Table Scan
SELECT * FROM PRODUCTS 
WHERE PRODUCT_NAME LIKE '%키워드%';

-- ✅ 개선: Full Text Index 또는 검색 엔진(Elasticsearch) 사용 권장
```

### 3. 집계 쿼리 비정규화
```sql
-- 통계 데이터는 컬럼으로 저장
VIEW_COUNT, SALE_COUNT, RATING_AVG, REVIEW_COUNT
-- 실시간 계산 대신 이벤트 발생 시 업데이트
```

### 4. 페이징 최적화
```sql
-- ✅ Good: LIMIT + OFFSET
SELECT * FROM PRODUCTS 
WHERE STATUS = 'ON_SALE'
ORDER BY CREATED_AT DESC
LIMIT 20 OFFSET 0;

-- 대용량 데이터는 커서 기반 페이징 고려
SELECT * FROM PRODUCTS 
WHERE STATUS = 'ON_SALE' AND CREATED_AT < '2024-01-01'
ORDER BY CREATED_AT DESC
LIMIT 20;
```

---

## 📋 다음 단계

1. **ERD 도구로 시각화**
   - https://dbdiagram.io 에서 DBML 파일 업로드
   - 테이블 관계 확인

2. **DDL 생성**
   - MySQL/MariaDB용 CREATE TABLE 문 생성
   - 인덱스, 외래키 생성

3. **샘플 데이터 작성**
   - 테스트용 더미 데이터 준비

4. **API 설계**
   - RESTful API 엔드포인트 정의
   - 요청/응답 DTO 설계

5. **Entity 클래스 작성**
   - JPA Entity 생성
   - 연관관계 매핑

---

## ✅ 체크리스트

- [x] 사용자 관리 강화
- [x] 배송지 관리 추가
- [x] 카테고리 시스템 추가
- [x] 상품 옵션/이미지 확장
- [x] 장바구니/위시리스트 추가
- [x] 주문 상태 이력 관리
- [x] 배송 추적 시스템
- [x] 환불 프로세스 추가
- [x] 쿠폰 시스템 구축
- [x] 리뷰 시스템 구축
- [x] 문의 관리 시스템
- [x] 알림 시스템 추가
- [x] 통계/로그 수집
- [x] AI 서비스 준비
- [x] 인덱스 최적화
- [x] 슬로우 쿼리 방지 전략

모든 필수 기능이 ERD에 반영되었습니다! 🎉
