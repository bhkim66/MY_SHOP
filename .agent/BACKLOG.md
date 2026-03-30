# MY_SHOP 개발 백로그

> 마지막 업데이트: 2026-03-29
> Phase 1 MVP 완료 기준으로 작성된 잔여 작업 목록

---

## 상태 표시

| 아이콘 | 상태 |
|--------|------|
| 🔴 | TODO (미시작) |
| 🟡 | IN PROGRESS (진행 중) |
| 🟢 | DONE (완료) |

---

## Phase 1 완료 현황

| 항목 | 상태 |
|------|------|
| JWT 로그인 / 회원가입 | 🟢 DONE |
| 마켓 자동 생성 (SELLER 가입 시) | 🟢 DONE |
| 상품 등록 / 조회 (판매자) | 🟢 DONE |
| 상품 목록 / 상세 조회 (구매자 공개) | 🟢 DONE |
| 주문 생성 / 조회 / 취소 (구매자) | 🟢 DONE |
| 결제 연동 (Mock PG) | 🟢 DONE |
| 판매자 대시보드 통계 | 🟢 DONE |
| 판매자 주문 관리 (상태 변경) | 🟢 DONE |

---

## Phase 2 백로그

### 🛒 BL-01 장바구니 (Cart)
**우선순위**: 높음
**영향 범위**: 백엔드 + 프론트엔드

#### 백엔드
- [x] `CartRepository` 생성 (`order/infrastructure`)
- [x] `CartService` 구현 (`order/application`)
  - 장바구니 담기 (재고 초과 불가)
  - 수량 변경
  - 항목 삭제
  - 전체 조회
- [x] `CartController` 구현 (`order/interfaces`)
  - `POST /v1/cart` — 상품 담기
  - `GET /v1/cart` — 장바구니 조회
  - `PATCH /v1/cart/{cartSeq}` — 수량 변경
  - `DELETE /v1/cart/{cartSeq}` — 항목 삭제
- [x] API 명세서 업데이트 (`API_SPECIFICATION.md`)
- [x] 테스트 작성 (Service 단위, Controller MockMvc)

#### 프론트엔드
- [x] `CartPage.jsx` 구현 (`pages/client/`)
- [x] `CartItem.jsx` 컴포넌트 구현 (`components/cart/`)
- [x] `CartSummary.jsx` 컴포넌트 구현 (합계 금액, 주문하기 버튼)
- [x] `cart.api.js` API 모듈 추가 또는 `buyer.api.js`에 병합
- [x] Header 장바구니 아이콘 + 개수 뱃지 연동
- [x] 장바구니 → 주문서 페이지 연결 (`OrderFormPage`)

---

### ❤️ BL-02 찜하기 (Wishlist)
**우선순위**: 낮음
**영향 범위**: 백엔드 + 프론트엔드

#### 백엔드
- [ ] `WishlistRepository` 생성
- [ ] `WishlistService` 구현 (추가/삭제/조회, 중복 추가 방지)
- [ ] `WishlistController` 구현
  - `POST /v1/wishlist/{productSeq}` — 찜 추가/해제 (토글)
  - `GET /v1/wishlist` — 내 찜 목록

#### 프론트엔드
- [ ] 상품 카드/상세 페이지에 찜 버튼 추가
- [ ] `WishlistPage.jsx` 구현

---

### ⭐ BL-03 리뷰 시스템
**우선순위**: 높음
**영향 범위**: 백엔드 + 프론트엔드
**전제 조건**: 주문 상태 `DELIVERED` 이후에만 작성 가능

#### 백엔드
- [ ] `ReviewRepository`, `ReviewImageRepository`, `ReviewHelpfulRepository` 생성
- [ ] `ReviewService` 구현
  - 리뷰 작성 (배송완료 주문만 가능, 1주문 1리뷰)
  - 리뷰 수정 / 삭제
  - 상품별 리뷰 목록 조회 (페이지네이션)
  - 도움이 돼요 토글
- [ ] `ReviewController` 구현
  - `POST /v1/reviews` — 리뷰 작성
  - `GET /v1/products/{productSeq}/reviews` — 상품 리뷰 목록
  - `PUT /v1/reviews/{reviewSeq}` — 리뷰 수정
  - `DELETE /v1/reviews/{reviewSeq}` — 리뷰 삭제
  - `POST /v1/reviews/{reviewSeq}/helpful` — 도움이 돼요 토글
- [ ] 상품 평점 자동 집계 (Product 엔티티 `avgRating`, `reviewCount` 업데이트)

#### 프론트엔드
- [ ] `ReviewList.jsx` 컴포넌트 (상품 상세 페이지 하단)
- [ ] `ReviewForm.jsx` 컴포넌트 (별점 선택, 이미지 업로드)
- [ ] 주문 상세 페이지에 리뷰 작성 버튼 연결

---

### 💬 BL-04 상품 문의 (Inquiry)
**우선순위**: 중간
**영향 범위**: 백엔드 + 프론트엔드

#### 백엔드
- [ ] `InquiryRepository`, `InquiryAnswerRepository` 생성
- [ ] `InquiryService` 구현
  - 문의 작성 (구매자)
  - 문의 목록 조회 (상품별, 내 문의별)
  - 문의 답변 작성 (판매자)
  - 문의 삭제
- [ ] `InquiryController` 구현
  - `POST /v1/inquiries` — 문의 작성
  - `GET /v1/products/{productSeq}/inquiries` — 상품 문의 목록
  - `GET /v1/inquiries` — 내 문의 목록
  - `POST /v1/seller/inquiries/{inquirySeq}/answer` — 답변 작성

#### 프론트엔드
- [ ] `InquiryList.jsx` / `InquiryForm.jsx` 컴포넌트 (상품 상세 페이지)
- [ ] 판매자 문의 관리 페이지 (`pages/seller/InquiryPage.jsx`)

---

### 🚚 BL-05 배송 관리 (Delivery)
**우선순위**: 높음
**영향 범위**: 백엔드 + 프론트엔드
**전제 조건**: 판매자 주문 관리 기능 연계

#### 백엔드
- [x] `DeliveryService` 구현
  - 운송장 등록 (주문 상태 → `SHIPPING` 자동 변경)
  - 배송 조회 (구매자)
- [x] `DeliveryController` 구현
  - `POST /v1/seller/orders/{orderSeq}/shipment` — 운송장 등록
  - `GET /v1/orders/{orderSeq}/shipment` — 배송 조회 (구매자)
- [x] 주문 상태 흐름 완성: `CONFIRMED → PREPARING → SHIPPING → DELIVERED`
- [x] 주문 취소 가능 상태 재정의 (`SHIPPING` 이전만)

#### 프론트엔드
- [x] 판매자 주문 상세 모달에 운송장 입력 폼 추가
- [x] 구매자 주문 상세 페이지에 배송 추적 정보 표시

---

### 🔐 BL-06 OAuth 2.0 소셜 로그인
**우선순위**: 중간
**영향 범위**: 백엔드 + 프론트엔드

#### 백엔드
- [ ] `build.gradle`에 OAuth 의존성 추가 (`spring-boot-starter-oauth2-client`)
- [ ] `application.yml`에 Google / Kakao 클라이언트 키 설정
- [ ] `CustomOAuth2UserService` 구현
  - 소셜 로그인 시 `UserSocialAccount` 연동
  - 신규 유저 자동 회원가입 처리
  - 기존 계정과 이메일 매핑
- [ ] OAuth 성공 핸들러 — JWT 발급 후 프론트 리다이렉트
- [ ] `SecurityConfig` OAuth 설정 추가

#### 프론트엔드
- [ ] 로그인 페이지에 Google / Kakao 버튼 추가
- [ ] OAuth 콜백 처리 (토큰 수신 및 저장)

---

### 💳 BL-07 실제 PG 결제 연동 (토스페이먼츠)
**우선순위**: 중간
**영향 범위**: 백엔드 + 프론트엔드

#### 백엔드
- [ ] 토스페이먼츠 SDK / REST API 연동
- [ ] 결제 승인 API 구현 (`POST /v1/payments/toss/confirm`)
- [ ] 결제 실패 처리 및 주문 상태 롤백
- [ ] 결제 취소 / 환불 API 구현
  - `POST /v1/payments/{paymentSeq}/cancel`
- [ ] `Refund` 엔티티 활용한 환불 이력 저장
- [ ] 결제 금액 위변조 방지 검증 강화

#### 프론트엔드
- [ ] 토스페이먼츠 SDK 설치 및 결제창 연동
- [ ] 결제 성공/실패 콜백 처리 페이지

---

### 📊 BL-08 정산 시스템 (Settlement)
**우선순위**: 낮음
**영향 범위**: 백엔드 + 프론트엔드

#### 백엔드
- [ ] `SettlementRepository`, `SettlementOrderRepository`, `FeePolicyRepository` 생성
- [ ] `SettlementService` 구현
  - 구매 확정 건 정산 집계 (수수료 3% 공제)
  - 월별 정산 내역 조회
  - 정산 상태 관리 (`PENDING` → `COMPLETED`)
- [ ] 정산 Controller 구현
  - `GET /v1/seller/settlements` — 정산 목록
  - `GET /v1/seller/settlements/{settlementSeq}` — 정산 상세

#### 프론트엔드
- [ ] 판매자 정산 페이지 (`pages/seller/SettlementPage.jsx`)

---

### 🎟️ BL-09 쿠폰 / 프로모션 (Promotion)
**우선순위**: 낮음
**영향 범위**: 백엔드 + 프론트엔드

#### 백엔드
- [ ] `CouponRepository`, `UserCouponRepository` 생성
- [ ] `PromotionService` 구현
  - 쿠폰 발급 (관리자)
  - 쿠폰 사용 (주문 시 적용, 사용 조건 검증)
  - 내 쿠폰 목록 조회
- [ ] 주문 생성 로직에 쿠폰 할인 적용

#### 프론트엔드
- [ ] 주문서 페이지에 쿠폰 선택 UI 추가
- [ ] 마이페이지 쿠폰 목록 페이지

---

### 🔔 BL-10 알림 시스템 (Notification)
**우선순위**: 낮음
**영향 범위**: 백엔드 + 프론트엔드

#### 백엔드
- [ ] `NotificationRepository` 생성
- [ ] `NotificationService` 구현
  - 주문 생성 시 판매자 알림
  - 배송 시작 시 구매자 알림
  - 문의 답변 등록 시 구매자 알림
- [ ] 알림 Controller 구현
  - `GET /v1/notifications` — 내 알림 목록
  - `PATCH /v1/notifications/{notificationSeq}/read` — 읽음 처리

#### 프론트엔드
- [ ] Header 알림 아이콘 + 읽지 않은 수 뱃지
- [ ] 알림 드롭다운 목록

---

### 🤖 BL-11 AI 마켓 설명 생성
**우선순위**: 낮음
**영향 범위**: 백엔드 + 프론트엔드

#### 백엔드
- [ ] `AiMarketDescriptionRepository` 생성
- [ ] OpenAI API 클라이언트 구현 (`ai/infrastructure`)
- [ ] `AiService` 구현
  - 상품명 + 키워드 입력 → GPT 설명 생성
  - 생성 이력 저장 (`AiMarketDescription`)
- [ ] AI Controller 구현
  - `POST /v1/ai/description` — 설명 생성 요청

#### 프론트엔드
- [ ] 상품 등록 폼에 AI 설명 생성 버튼 추가
- [ ] 생성된 설명 미리보기 및 수정 UI

---

### 🏪 BL-12 마켓 설정 관리 (Market)
**우선순위**: 중간
**영향 범위**: 백엔드 + 프론트엔드

#### 백엔드
- [ ] `MarketService` 구현 (마켓 정보 수정, 배송 정책 설정)
- [ ] `MarketController` 구현
  - `GET /v1/seller/market` — 내 마켓 정보 조회
  - `PUT /v1/seller/market` — 마켓 정보 수정

#### 프론트엔드
- [ ] 판매자 마켓 설정 페이지 (`pages/seller/MarketSettingPage.jsx`)

---

### 👤 BL-13 마이페이지 / 배송지 관리
**우선순위**: 중간
**영향 범위**: 백엔드 + 프론트엔드

#### 백엔드
- [ ] `UserAddressRepository` 생성
- [ ] 배송지 관리 API 구현
  - `GET /v1/members/addresses` — 배송지 목록
  - `POST /v1/members/addresses` — 배송지 추가
  - `PUT /v1/members/addresses/{addressSeq}` — 배송지 수정
  - `DELETE /v1/members/addresses/{addressSeq}` — 배송지 삭제
  - `PATCH /v1/members/addresses/{addressSeq}/default` — 기본 배송지 설정
- [ ] 회원 정보 수정 API (이름, 휴대폰 변경)
- [ ] 회원 탈퇴 API (진행 중 주문 있으면 거부)

#### 프론트엔드
- [ ] `MyPage.jsx` 구현 (회원 정보 조회/수정)
- [ ] `AddressListPage.jsx` 구현 (배송지 관리)
- [ ] 주문서 페이지에서 저장된 배송지 선택 기능

---

### 🔍 BL-14 상품 검색 / 필터 고도화
**우선순위**: 중간
**영향 범위**: 백엔드 + 프론트엔드

#### 백엔드
- [ ] QueryDSL 동적 쿼리로 상품 검색 구현
  - 키워드 검색 (상품명, 설명)
  - 카테고리 필터
  - 가격 범위 필터
  - 정렬 (최신순 / 판매량순 / 가격 낮은순 / 가격 높은순)
- [ ] `SearchLog` 저장 로직 추가

#### 프론트엔드
- [ ] 상품 목록 페이지에 검색 바 추가
- [ ] 카테고리 사이드바 / 필터 UI
- [ ] 정렬 드롭다운

---

## 기술 부채 / 품질 개선

### 🧪 BL-T01 테스트 코드 보강
**우선순위**: 높음

- [x] `BuyerOrderServiceTest` — 주문 생성, 취소, 권한 검증
- [x] `PaymentServiceTest` — 결제 요청, 확인, 금액 검증
- [ ] `ProductServiceTest` — 상품 등록, 재고 차감, 유효성
- [x] `DashboardServiceTest` — 통계 집계 로직
- [x] `CartServiceTest` — 장바구니 담기, 수량 변경, 삭제, 개수 조회
- [x] `BuyerOrderControllerTest` — MockMvc 웹 계층 테스트
- [ ] `PaymentControllerTest`
- [ ] `SellerProductManagementControllerTest`

### ⚡ BL-T02 재고 동시성 제어
**우선순위**: 높음

- [ ] `ProductOption` 재고 차감 시 낙관적 잠금(`@Version`) 또는 비관적 잠금(`PESSIMISTIC_WRITE`) 적용
- [ ] 동시 주문 시나리오 통합 테스트 작성

### 📱 BL-T03 반응형 / 모바일 최적화
**우선순위**: 낮음

- [ ] 모바일 네비게이션 (햄버거 메뉴)
- [ ] 상품 그리드 반응형 레이아웃
- [ ] 주문서 / 결제 페이지 모바일 UX 개선

### 🎨 BL-T04 프론트엔드 UX 개선
**우선순위**: 낮음

- [ ] Toast 알림 컴포넌트 (성공/에러 메시지)
- [ ] 전역 Loading Spinner 개선
- [ ] 에러 바운더리 추가
- [ ] Skeleton UI (상품 목록 로딩 중)

---

## 우선순위 요약

| 순위 | 항목 | 이유 |
|------|------|------|
| 1 | BL-01 장바구니 | 다중 상품 주문을 위한 핵심 기능 |
| 2 | BL-05 배송 관리 | 주문 상태 흐름 완성에 필수 |
| 3 | BL-T01 테스트 코드 | 품질 보증 기반 |
| 4 | BL-03 리뷰 시스템 | 구매자 구매 결정 지원 |
| 5 | BL-T02 재고 동시성 | 운영 안정성 |
| 6 | BL-06 OAuth 2.0 | 회원 가입 허들 감소 |
| 7 | BL-07 실제 PG 결제 | 실 서비스 전환에 필수 |
| 8 | BL-13 마이페이지 | 배송지 재활용 편의성 |
| 9 | BL-12 마켓 설정 | 판매자 운영 편의성 |
| 10 | BL-14 상품 검색 | 구매자 탐색 경험 개선 |
| 11 | BL-04 상품 문의 | 판매자-구매자 소통 채널 |
| 12 | BL-08 정산 시스템 | 수익 관리 |
| 13 | BL-11 AI 설명 생성 | 차별화 기능 |
| 14 | BL-09 쿠폰/프로모션 | 마케팅 기능 |
| 15 | BL-10 알림 시스템 | 편의 기능 |
| 16 | BL-02 찜하기 | 부가 기능 |
