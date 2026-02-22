# Phase 1 MVP - 주문/결제 구현 완료

## 개요

Phase 1 MVP에서는 기본적인 이커머스 플로우인 **상품 조회 → 주문 생성 → 결제 → 주문 관리**를 구현했습니다.

구현 날짜: 2026년 2월

---

## 구현된 기능

### 1. 구매자 상품 API (공개)

인증 없이 누구나 접근 가능한 공개 API로, 고객이 상품을 탐색할 수 있습니다.

#### API 엔드포인트
- `GET /v1/products` - 상품 목록 조회 (페이지네이션)
- `GET /v1/products/{seq}` - 상품 상세 조회 (조회수 자동 증가)

#### 주요 파일
- **Controller**: `buyer/interfaces/controller/BuyerProductController.java`
- **Service**: `buyer/application/BuyerProductService.java`
- **DTO**:
  - `buyer/interfaces/dto/BuyerProductListResponse.java`
  - `buyer/interfaces/dto/BuyerProductDetailResponse.java`

#### 특징
- 페이지네이션 지원 (기본 12개/페이지)
- 최신순 정렬 (기본값: `createdAt DESC`)
- 상품 상세 조회 시 조회수 자동 증가
- 재고, 가격, 이미지 등 구매에 필요한 모든 정보 제공

---

### 2. 주문 API (인증 필요)

로그인한 사용자(BUYER, USER, SELLER, ADMIN)만 접근 가능한 API입니다.

#### API 엔드포인트
- `POST /v1/orders` - 주문 생성
- `GET /v1/orders` - 내 주문 목록 조회 (페이지네이션)
- `GET /v1/orders/{orderSeq}` - 주문 상세 조회
- `POST /v1/orders/{orderSeq}/cancel` - 주문 취소

#### 주요 파일
- **Controller**: `buyer/interfaces/controller/BuyerOrderController.java`
- **Service**: `buyer/application/BuyerOrderService.java`
- **DTO**:
  - `buyer/interfaces/dto/OrderCreateRequest.java`
  - `buyer/interfaces/dto/OrderCreateResponse.java`
  - `buyer/interfaces/dto/OrderListResponse.java`
  - `buyer/interfaces/dto/OrderDetailResponse.java`
- **Utility**: `common/utils/OrderNumberGenerator.java`

#### 주문 생성 프로세스
1. 주문 상품 정보 검증 (상품 존재 여부, 가격 일치 여부)
2. 재고 확인 및 차감
3. 주문 번호 자동 생성 (`ORD-YYYYMMDD-0001` 형식)
4. Order 엔티티 생성 (상태: PENDING)
5. OrderItem 엔티티 생성 (주문 상품 정보)
6. 배송 정보 저장

#### 주문 취소 프로세스
1. 주문 소유권 검증 (본인 주문만 취소 가능)
2. 취소 가능 상태 확인 (PENDING, CONFIRMED만 취소 가능)
3. 주문 상태를 CANCELED로 변경
4. 재고 복구
5. 결제 취소 처리 (결제 완료된 경우)

#### 특징
- JWT 인증을 통한 사용자 검증
- 주문 번호 자동 생성 (날짜 기반 일련번호)
- 재고 자동 관리 (주문 시 차감, 취소 시 복구)
- 본인 주문만 조회/취소 가능 (보안)
- 주문 상태별 취소 가능 여부 검증

---

### 3. 결제 API (Mock 구현)

현재는 Mock으로 구현되어 있으며, 추후 실제 PG사 연동 예정입니다.

#### API 엔드포인트
- `POST /v1/payments` - 결제 요청
- `POST /v1/payments/{paymentSeq}/confirm` - 결제 확인 (승인)

#### 주요 파일
- **Controller**: `payment/interfaces/controller/PaymentController.java`
- **Service**: `payment/application/PaymentService.java`
- **Repository**: `payment/infrastructure/PaymentRepository.java`
- **Entity**: `payment/domain/Payment.java`
- **DTO**:
  - `payment/interfaces/dto/PaymentRequest.java`
  - `payment/interfaces/dto/PaymentResponse.java`
  - `payment/interfaces/dto/PaymentConfirmResponse.java`

#### 결제 프로세스
1. 결제 요청 (주문 ID, 결제 수단, 금액)
2. 주문 금액과 결제 금액 일치 여부 검증
3. Payment 엔티티 생성 (상태: PENDING)
4. 결제 확인 API 호출
5. 결제 상태를 COMPLETED로 변경
6. 주문 상태를 CONFIRMED로 변경

#### 지원 결제 수단
- `CARD` - 카드 결제
- `BANK_TRANSFER` - 계좌이체
- `VIRTUAL_ACCOUNT` - 가상계좌

#### Mock 구현 특징
- 항상 성공하는 Mock 구현
- 실제 PG사 연동을 위한 인터페이스 설계
- 결제 완료 시 주문 상태 자동 변경

---

## 프론트엔드 구현

### 신규 페이지 (8개)

#### 고객용 페이지 (`/src/pages/client/`)

1. **ProductListPage.jsx**
   - 기능: 상품 목록 조회, 페이지네이션
   - API: `GET /v1/products`
   - 특징: 상품 그리드 레이아웃, 카드 클릭 시 상세 페이지 이동

2. **ProductDetailPage.jsx**
   - 기능: 상품 상세 정보, 수량 선택, 구매하기
   - API: `GET /v1/products/{seq}`
   - 특징: 이미지 갤러리, 상품 정보 표시, 주문서 작성으로 이동

3. **OrderFormPage.jsx**
   - 기능: 주문서 작성, 배송지 입력
   - API: 없음 (로컬 상태 관리)
   - 특징: AddressForm 컴포넌트 사용, 우편번호 검색 (Daum 주소 API)

4. **PaymentPage.jsx**
   - 기능: 결제 수단 선택, 결제 진행
   - API: `POST /v1/orders`, `POST /v1/payments`, `POST /v1/payments/{seq}/confirm`
   - 특징: 주문 생성 → 결제 요청 → 결제 확인 플로우

5. **OrderCompletePage.jsx**
   - 기능: 주문 완료 안내, 주문 정보 표시
   - API: 없음 (라우터 state로 정보 전달)
   - 특징: 주문 번호, 총 금액 표시, 주문 목록 이동 버튼

6. **MyOrdersPage.jsx**
   - 기능: 내 주문 목록 조회
   - API: `GET /v1/orders`
   - 특징: 페이지네이션, 주문 상태별 뱃지, 상세 페이지 이동

7. **OrderDetailPage.jsx**
   - 기능: 주문 상세 정보, 주문 취소
   - API: `GET /v1/orders/{seq}`, `POST /v1/orders/{seq}/cancel`
   - 특징: 주문 상품 목록, 배송 정보, 취소 기능

8. **HomePage.jsx** (기존)
   - 기능: 메인 페이지, 상품 목록 이동
   - 특징: 히어로 배너, 상품 섹션 (준비 중)

### 신규 컴포넌트 (5개)

#### 상품 컴포넌트 (`/src/components/product/`)

1. **ProductCard.jsx**
   - 기능: 상품 카드 UI
   - Props: `product` (상품 정보)
   - 특징: 썸네일, 상품명, 가격, 재고/판매 정보 표시

2. **ProductGrid.jsx**
   - 기능: 상품 그리드 레이아웃
   - Props: `products` (상품 배열)
   - 특징: 반응형 그리드 (Bootstrap col-md-3)

#### 주문 컴포넌트 (`/src/components/order/`)

3. **AddressForm.jsx**
   - 기능: 배송지 입력 폼
   - Props: `formData`, `onChange`
   - 특징: 우편번호 검색 (Daum 주소 API), 유효성 검증

4. **OrderItemList.jsx**
   - 기능: 주문 상품 목록 표시
   - Props: `items` (주문 상품 배열)
   - 특징: 상품 이미지, 상품명, 수량, 가격 표시

5. **OrderStatusBadge.jsx**
   - 기능: 주문 상태 뱃지
   - Props: `status` (주문 상태)
   - 특징: 상태별 색상 구분 (PENDING: warning, CONFIRMED: info, CANCELED: danger, etc.)

### API 연동 모듈

#### buyer.api.js (`/src/api/buyer.api.js`)

모든 구매자 관련 API 호출을 담당하는 모듈입니다.

**주요 함수**:
```javascript
// 상품
getProducts(page, size)           // 상품 목록 조회
getProductDetail(productSeq)      // 상품 상세 조회

// 주문
createOrder(orderData)            // 주문 생성
getMyOrders(page, size)           // 내 주문 목록 조회
getOrderDetail(orderSeq)          // 주문 상세 조회
cancelOrder(orderSeq, reason)     // 주문 취소

// 결제
requestPayment(paymentData)       // 결제 요청
confirmPayment(paymentSeq)        // 결제 확인
```

---

## 데이터베이스 스키마

### 주요 테이블

#### orders (주문)
```sql
CREATE TABLE orders (
    seq BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_number VARCHAR(50) UNIQUE NOT NULL,
    buyer_seq BIGINT NOT NULL,
    total_amount INT NOT NULL,
    order_status VARCHAR(20) NOT NULL,
    receiver_name VARCHAR(100) NOT NULL,
    receiver_phone VARCHAR(20) NOT NULL,
    receiver_address VARCHAR(255) NOT NULL,
    receiver_address_detail VARCHAR(255),
    receiver_zipcode VARCHAR(10) NOT NULL,
    memo VARCHAR(500),
    ordered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (buyer_seq) REFERENCES members(seq)
);
```

#### order_items (주문 상품)
```sql
CREATE TABLE order_items (
    seq BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_seq BIGINT NOT NULL,
    product_seq BIGINT NOT NULL,
    quantity INT NOT NULL,
    price INT NOT NULL,
    FOREIGN KEY (order_seq) REFERENCES orders(seq),
    FOREIGN KEY (product_seq) REFERENCES products(seq)
);
```

#### payments (결제)
```sql
CREATE TABLE payments (
    seq BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_seq BIGINT NOT NULL,
    buyer_seq BIGINT NOT NULL,
    payment_method VARCHAR(20) NOT NULL,
    amount INT NOT NULL,
    status VARCHAR(20) NOT NULL,
    requested_at TIMESTAMP,
    confirmed_at TIMESTAMP,
    FOREIGN KEY (order_seq) REFERENCES orders(seq),
    FOREIGN KEY (buyer_seq) REFERENCES members(seq)
);
```

---

## 주요 비즈니스 로직

### 주문 번호 생성

**구현 위치**: `common/utils/OrderNumberGenerator.java`

```java
public static String generate() {
    String datePrefix = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    String randomSuffix = String.format("%04d", new Random().nextInt(10000));
    return "ORD-" + datePrefix + "-" + randomSuffix;
}
```

**형식**: `ORD-YYYYMMDD-0001`

### 재고 관리

**주문 생성 시**:
```java
product.decreaseStock(item.getQuantity());
```

**주문 취소 시**:
```java
for (OrderItem item : orderItems) {
    Product product = productRepository.findById(item.getProductSeq())
        .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다"));
    product.increaseStock(item.getQuantity());
}
```

### 주문 상태 관리

**주문 상태**:
- `PENDING` - 주문 대기 (결제 전)
- `CONFIRMED` - 결제 완료
- `SHIPPED` - 배송 중 (추후 구현)
- `DELIVERED` - 배송 완료 (추후 구현)
- `CANCELED` - 주문 취소

**취소 가능 상태**: `PENDING`, `CONFIRMED`

---

## 보안 구현

### 인증 및 권한

1. **JWT 인증**
   - Spring Security + JWT 사용
   - `@PreAuthorize` 어노테이션으로 권한 제어
   - UserDetails를 통한 사용자 정보 추출

2. **주문 소유권 검증**
   ```java
   if (!order.getBuyerSeq().equals(buyerSeq)) {
       throw new IllegalArgumentException("본인의 주문만 조회/취소할 수 있습니다");
   }
   ```

3. **결제 검증**
   - 주문 금액과 결제 금액 일치 여부 확인
   - 본인의 주문/결제만 처리 가능

### API 권한 설정

- **공개 API** (인증 불필요):
  - `/v1/products` - 상품 목록
  - `/v1/products/{seq}` - 상품 상세

- **인증 필요 API**:
  - `/v1/orders/**` - BUYER, USER, SELLER, ADMIN
  - `/v1/payments/**` - BUYER, USER, SELLER, ADMIN

- **판매자 전용 API**:
  - `/v1/seller/**` - SELLER, ADMIN

---

## 테스트

### API 테스트

**상품 조회**:
```bash
# 상품 목록
curl http://localhost:8080/v1/products

# 상품 상세
curl http://localhost:8080/v1/products/1
```

**주문 생성** (인증 필요):
```bash
curl -X POST http://localhost:8080/v1/orders \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "items": [{"productSeq": 1, "quantity": 2, "price": 29000}],
    "receiverName": "홍길동",
    "receiverPhone": "010-1234-5678",
    "receiverAddress": "서울시 강남구 테헤란로 123",
    "receiverZipcode": "06234"
  }'
```

**결제**:
```bash
# 결제 요청
curl -X POST http://localhost:8080/v1/payments \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "orderSeq": 1,
    "paymentMethod": "CARD",
    "amount": 58000
  }'

# 결제 확인
curl -X POST http://localhost:8080/v1/payments/1/confirm \
  -H "Authorization: Bearer {token}"
```

---

## 향후 개선 사항

### 백엔드

1. **실제 PG사 연동**
   - 토스페이먼츠, 카카오페이 등 연동
   - 결제 승인/실패 처리
   - 결제 취소/환불 처리

2. **주문 상태 관리 고도화**
   - 배송 상태 추가 (SHIPPED, DELIVERED)
   - 판매자의 주문 상태 변경 API
   - 배송 추적 번호 관리

3. **재고 관리 개선**
   - 동시성 제어 (낙관적 잠금/비관적 잠금)
   - 재고 부족 시 예외 처리 강화

4. **알림 시스템**
   - 주문 생성 시 판매자 알림
   - 배송 시작 시 구매자 알림
   - 이메일/SMS 알림

### 프론트엔드

1. **UX 개선**
   - 로딩 스피너 추가
   - 에러 메시지 개선
   - Toast 알림 추가

2. **장바구니 기능**
   - 여러 상품 동시 주문
   - 장바구니 저장 (로컬스토리지)

3. **주문 관리 기능 확장**
   - 주문 상태별 필터링
   - 주문 검색 (주문번호, 상품명)
   - 재주문 기능

4. **반응형 디자인 개선**
   - 모바일 최적화
   - 태블릿 최적화

---

## 참고 문서

- [백엔드 API 명세서](/Users/bh/home/10.project/MY_SHOP/.agent/API_SPECIFICATION.md)
- [프론트엔드 README](/Users/bh/home/10.project/MY_SHOP_FRONT/README.md)
- [ERD 구조](/Users/bh/home/10.project/MY_SHOP/.agent/ENTITY_STRUCTURE.md)

---

**작성일**: 2026년 2월 22일
**상태**: Phase 1 MVP 완료
