# API 명세서

MY_SHOP 프로젝트의 모든 REST API 엔드포인트를 문서화합니다.

---

## 목차
- [인증(Authentication)](#인증authentication)
- [회원 관리 API](#회원-관리-api)
- [카테고리 API](#카테고리-api)
- [구매자 상품 API (PUBLIC)](#구매자-상품-api-public)
- [주문 API (BUYER)](#주문-api-buyer)
- [결제 API (MOCK)](#결제-api-mock)
- [상품 관리 API (SELLER)](#상품-관리-api-seller)
- [대시보드 API (SELLER)](#대시보드-api-seller)
- [주문 관리 API (SELLER)](#주문-관리-api-seller)

---

## 기본 정보

**Base URL**: `http://localhost:8080`

**Content-Type**: `application/json` (파일 업로드 제외)

---

## 인증(Authentication)

### 인증 방식
- JWT Bearer Token 사용
- 요청 헤더에 `Authorization: Bearer {access_token}` 포함

### 1. 로그인
```http
POST /v1/auth/login
Content-Type: application/json
```

**Request Body**:
```json
{
  "email": "user@test.com",
  "password": "password"
}
```

**Response (200 OK)**:
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "grantType": "Bearer"
}
```

### 2. 토큰 재발급
```http
POST /v1/auth/reissue
Content-Type: application/json
```

**Request Body**:
```json
{
  "accessToken": "expired_access_token",
  "refreshToken": "valid_refresh_token"
}
```

**Response (200 OK)**:
```json
{
  "accessToken": "new_access_token",
  "refreshToken": "new_refresh_token",
  "grantType": "Bearer"
}
```

### 3. 내 정보 조회
```http
GET /v1/auth/me
Authorization: Bearer {access_token}
```

**Response (200 OK)**:
```json
{
  "seq": 1,
  "email": "user@test.com",
  "name": "홍길동",
  "phone": "010-1234-5678",
  "role": "BUYER"
}
```

---

## 회원 관리 API

### 1. 회원가입
```http
POST /v1/members/signup
Content-Type: application/json
```

**Request Body**:
```json
{
  "email": "newuser@test.com",
  "password": "password123",
  "name": "홍길동",
  "phone": "010-1234-5678",
  "role": "BUYER"
}
```

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| email | String | O | 이메일 (로그인 ID로 사용) |
| password | String | O | 비밀번호 |
| name | String | O | 이름 |
| phone | String | O | 전화번호 |
| role | String | O | 역할 (BUYER, SELLER) |

**Response (200 OK)**:
```json
{
  "seq": 1,
  "email": "newuser@test.com",
  "name": "홍길동",
  "phone": "010-1234-5678",
  "role": "BUYER"
}
```

> [!NOTE]
> SELLER로 가입 시 자동으로 Market이 생성됩니다.

---

## 카테고리 API

> [!NOTE]
> 카테고리 API는 **인증 없이** 호출 가능합니다. (`SecurityConfig`에서 `/v1/categories/**` 경로를 `permitAll` 처리)

### 1. 전체 카테고리 목록 조회

```http
GET /v1/categories
```

**Request**: 없음

**Response (200 OK)**:
```json
[
  {
    "seq": 1,
    "categoryCode": "FASHION",
    "categoryName": "패션/의류",
    "depth": 1,
    "sortOrder": 1
  },
  {
    "seq": 6,
    "categoryCode": "FASHION_TOP",
    "categoryName": "상의",
    "depth": 2,
    "sortOrder": 1
  },
  {
    "seq": 7,
    "categoryCode": "FASHION_BOTTOM",
    "categoryName": "하의",
    "depth": 2,
    "sortOrder": 2
  }
]
```

**Response 필드**:
| 필드 | 타입 | 설명 |
|------|------|------|
| seq | Long | 카테고리 ID |
| categoryCode | String | 카테고리 코드 (고유값) |
| categoryName | String | 카테고리 표시명 |
| depth | Integer | 계층 깊이 (1: 최상위, 2: 하위) |
| sortOrder | Integer | 같은 계층 내 정렬 순서 |

> [!NOTE]
> - `is_display = true`인 카테고리만 반환됩니다.
> - 응답 목록은 `sort_order` 오름차순으로 정렬됩니다.
> - 부모 카테고리 참조(`parent_seq`)는 응답에 포함되지 않습니다. 계층 구조 구성이 필요한 경우 클라이언트에서 `seq`를 키로 매핑하여 처리합니다.

---

## 구매자 상품 API (PUBLIC)

> [!NOTE]
> 구매자 상품 API는 **인증 없이** 호출 가능합니다. 일반 사용자가 상품을 조회할 수 있는 공개 API입니다.

### 1. 상품 목록 조회

```http
GET /v1/products?page=0&size=12&sort=createdAt,desc
```

**Query Parameters**:
| 파라미터 | 타입 | 필수 | 기본값 | 설명 |
|----------|------|------|--------|------|
| page | Integer | X | 0 | 페이지 번호 (0부터 시작) |
| size | Integer | X | 12 | 페이지 크기 |
| sort | String | X | createdAt,desc | 정렬 기준 (field,direction) |

**Response (200 OK)**:
```json
{
  "content": [
    {
      "seq": 1,
      "productName": "기본 면 티셔츠",
      "price": 29000,
      "salePrice": 25000,
      "thumbnailUrl": "https://via.placeholder.com/400",
      "marketName": "판매자's Shop",
      "categoryName": "상의",
      "reviewCount": 10,
      "ratingAvg": 4.5,
      "saleCount": 3
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 12
  },
  "totalPages": 1,
  "totalElements": 1,
  "last": true,
  "first": true
}
```

**Response 필드**:
| 필드 | 타입 | 설명 |
|------|------|------|
| seq | Long | 상품 ID |
| productName | String | 상품명 |
| price | Integer | 정가 (원) |
| salePrice | Integer | 판매가 (원) |
| thumbnailUrl | String | 썸네일 이미지 URL |
| marketName | String | 마켓명 |
| categoryName | String | 카테고리명 |
| reviewCount | Integer | 리뷰 수 |
| ratingAvg | BigDecimal | 평균 평점 |
| saleCount | Integer | 판매 수량 |

---

### 2. 상품 상세 조회

```http
GET /v1/products/{seq}
```

**Path Parameters**:
| 파라미터 | 타입 | 필수 | 설명 |
|----------|------|------|------|
| seq | Long | O | 상품 ID |

**Response (200 OK)**:
```json
{
  "seq": 1,
  "productName": "기본 면 티셔츠",
  "description": "편안한 착용감의 기본 면 티셔츠입니다",
  "price": 29000,
  "salePrice": 25000,
  "stockQty": 100,
  "minOrderQty": 1,
  "maxOrderQty": 10,
  "status": "ON_SALE",
  "thumbnailUrl": "https://via.placeholder.com/400",
  "imageUrls": [
    "https://via.placeholder.com/400",
    "https://via.placeholder.com/401"
  ],
  "marketName": "판매자's Shop",
  "marketSeq": 1,
  "categoryName": "상의",
  "categoryCode": "FASHION_TOP",
  "reviewCount": 10,
  "ratingAvg": 4.5,
  "viewCount": 15,
  "saleCount": 3
}
```

**Response 필드**:
| 필드 | 타입 | 설명 |
|------|------|------|
| seq | Long | 상품 ID |
| productName | String | 상품명 |
| description | String | 상품 설명 |
| price | Integer | 정가 (원) |
| salePrice | Integer | 판매가 (원) |
| stockQty | Integer | 재고 수량 |
| minOrderQty | Integer | 최소 주문 수량 |
| maxOrderQty | Integer | 최대 주문 수량 |
| status | String | 상품 상태 (ON_SALE, SOLD_OUT, HIDDEN) |
| thumbnailUrl | String | 썸네일 이미지 URL |
| imageUrls | List<String> | 상품 이미지 URL 목록 |
| marketName | String | 마켓명 |
| marketSeq | Long | 마켓 ID |
| categoryName | String | 카테고리명 |
| categoryCode | String | 카테고리 코드 |
| reviewCount | Integer | 리뷰 수 |
| ratingAvg | BigDecimal | 평균 평점 |
| viewCount | Integer | 조회수 |
| saleCount | Integer | 판매 수량 |

> [!NOTE]
> 상품 상세 조회 시 자동으로 조회수(viewCount)가 1 증가합니다.

---

## 주문 API (BUYER)

> [!IMPORTANT]
> 모든 주문 API는 **인증**이 필요합니다. (BUYER, USER, SELLER, ADMIN 권한)

### 1. 주문 생성

```http
POST /v1/orders
Authorization: Bearer {access_token}
Content-Type: application/json
```

**Request Body**:
```json
{
  "items": [
    {
      "productSeq": 1,
      "qty": 2
    }
  ],
  "receiverName": "홍길동",
  "receiverPhone": "010-1234-5678",
  "zipCode": "06234",
  "address1": "서울시 강남구 테헤란로 123",
  "address2": "456호",
  "shippingMessage": "부재 시 문 앞에 놓아주세요"
}
```

**Request 필드**:
| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| items | List | O | 주문 상품 목록 |
| items[].productSeq | Long | O | 상품 ID |
| items[].qty | Integer | O | 주문 수량 |
| receiverName | String | O | 받는 사람 이름 |
| receiverPhone | String | O | 받는 사람 전화번호 |
| zipCode | String | O | 우편번호 |
| address1 | String | O | 받는 주소 (기본) |
| address2 | String | X | 상세 주소 |
| shippingMessage | String | X | 배송 메모 |

**Response (200 OK)**:
```json
{
  "orderSeq": 1,
  "orderNo": "ORD-20260222-0001",
  "totalPayAmount": 58000,
  "orderStatus": "PENDING"
}
```

**Response 필드**:
| 필드 | 타입 | 설명 |
|------|------|------|
| orderSeq | Long | 주문 ID |
| orderNo | String | 주문 번호 |
| totalPayAmount | Integer | 총 결제 금액 (원) |
| orderStatus | String | 주문 상태 |

> [!NOTE]
> 주문 번호는 `ORD-YYYYMMDD-0001` 형식으로 자동 생성됩니다.

---

### 2. 내 주문 목록 조회

```http
GET /v1/orders?page=0&size=10&sort=orderedAt,desc
Authorization: Bearer {access_token}
```

**Query Parameters**:
| 파라미터 | 타입 | 필수 | 기본값 | 설명 |
|----------|------|------|--------|------|
| page | Integer | X | 0 | 페이지 번호 (0부터 시작) |
| size | Integer | X | 10 | 페이지 크기 |
| sort | String | X | orderedAt,desc | 정렬 기준 (field,direction) |

**Response (200 OK)**:
```json
{
  "content": [
    {
      "orderSeq": 1,
      "orderNo": "ORD-20260222-0001",
      "orderStatus": "PENDING",
      "totalPayAmount": 58000,
      "marketName": "판매자's Shop",
      "orderedAt": "2026-02-22T23:00:00",
      "firstItemName": "기본 면 티셔츠",
      "itemCount": 1
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10
  },
  "totalPages": 1,
  "totalElements": 1,
  "last": true,
  "first": true
}
```

**Response 필드**:
| 필드 | 타입 | 설명 |
|------|------|------|
| orderSeq | Long | 주문 ID |
| orderNo | String | 주문 번호 |
| orderStatus | String | 주문 상태 |
| totalPayAmount | Integer | 총 결제 금액 (원) |
| marketName | String | 마켓명 |
| orderedAt | LocalDateTime | 주문 일시 |
| firstItemName | String | 첫 번째 상품명 |
| itemCount | Integer | 총 주문 상품 종류 수 |

---

### 3. 주문 상세 조회

```http
GET /v1/orders/{orderSeq}
Authorization: Bearer {access_token}
```

**Path Parameters**:
| 파라미터 | 타입 | 필수 | 설명 |
|----------|------|------|------|
| orderSeq | Long | O | 주문 ID |

**Response (200 OK)**:
```json
{
  "orderSeq": 1,
  "orderNo": "ORD-20260222-0001",
  "orderStatus": "PENDING",
  "totalProductAmount": 58000,
  "totalDiscountAmount": 0,
  "couponDiscountAmount": 0,
  "shippingFee": 3000,
  "totalPayAmount": 61000,
  "receiverName": "홍길동",
  "receiverPhone": "010-1234-5678",
  "zipCode": "06234",
  "address1": "서울시 강남구 테헤란로 123",
  "address2": "456호",
  "shippingMessage": "부재 시 문 앞에 놓아주세요",
  "buyerName": "주문자",
  "buyerEmail": "buyer@test.com",
  "buyerPhone": "010-9999-8888",
  "marketName": "판매자's Shop",
  "orderedAt": "2026-02-22T23:00:00",
  "confirmedAt": null,
  "canceledAt": null,
  "cancelReason": null,
  "items": [
    {
      "seq": 1,
      "productSeq": 1,
      "itemName": "기본 면 티셔츠",
      "itemOption": "Blue/XL",
      "unitPrice": 29000,
      "qty": 2,
      "itemAmount": 58000,
      "discountAmount": 0,
      "itemStatus": "ORDERED",
      "thumbnailUrl": "https://via.placeholder.com/400"
    }
  ]
}
```

**Response 필드**:
| 필드 | 타입 | 설명 |
|------|------|------|
| orderSeq | Long | 주문 ID |
| orderNo | String | 주문 번호 |
| orderStatus | String | 주문 상태 |
| totalProductAmount | Integer | 원 상품 금액 합계 |
| totalDiscountAmount | Integer | 총 할인 금액 |
| couponDiscountAmount | Integer | 쿠폰 할인 금액 |
| shippingFee | Integer | 배송비 |
| totalPayAmount | Integer | 최종 결제 금액 |
| receiverName | String | 받는 사람 이름 |
| receiverPhone | String | 받는 사람 전화번호 |
| zipCode | String | 우편번호 |
| address1 | String | 받는 주소 (기본) |
| address2 | String | 상세 주소 |
| shippingMessage | String | 배송 메모 |
| buyerName | String | 주문자 이름 |
| buyerEmail | String | 주문자 이메일 |
| buyerPhone | String | 주문자 전화번호 |
| marketName | String | 마켓명 |
| orderedAt | LocalDateTime | 주문 일시 |
| confirmedAt | LocalDateTime | 주문 확정 일시 |
| canceledAt | LocalDateTime | 주문 취소 일시 |
| cancelReason | String | 취소 사유 |
| items | List | 주문 상품 목록 |
| items[].seq | Long | 주문 상품 항목 ID |
| items[].productSeq | Long | 상품 ID |
| items[].itemName | String | 상품명 |
| items[].itemOption | String | 선택 옵션 |
| items[].unitPrice | Integer | 단가 |
| items[].qty | Integer | 수량 |
| items[].itemAmount | Integer | 항목 금액 |
| items[].discountAmount | Integer | 항목 할인 금액 |
| items[].itemStatus | String | 항목 상태 |
| items[].thumbnailUrl | String | 상품 썸네일 URL |

> [!NOTE]
> 본인의 주문만 조회 가능합니다. 다른 사용자의 주문을 조회하면 403 Forbidden 오류가 발생합니다.

---

### 4. 주문 취소

```http
POST /v1/orders/{orderSeq}/cancel
Authorization: Bearer {access_token}
Content-Type: application/json
```

**Path Parameters**:
| 파라미터 | 타입 | 필수 | 설명 |
|----------|------|------|------|
| orderSeq | Long | O | 주문 ID |

**Request Body** (선택):
```json
{
  "reason": "단순 변심"
}
```

**Response (200 OK)**

> [!IMPORTANT]
> - PENDING, CONFIRMED 상태의 주문만 취소 가능합니다.
> - 취소 시 재고가 자동으로 복구됩니다.
> - 결제가 완료된 경우 결제 취소 처리도 함께 진행됩니다.

---

## 결제 API (MOCK)

> [!IMPORTANT]
> 결제 API는 **인증**이 필요합니다. (BUYER, USER, SELLER, ADMIN 권한)
>
> 현재는 Mock 구현으로, 실제 PG사 연동은 추후 진행됩니다.

### 1. 결제 요청

```http
POST /v1/payments
Authorization: Bearer {access_token}
Content-Type: application/json
```

**Request Body**:
```json
{
  "orderSeq": 1,
  "payMethod": "CARD",
  "payAmount": 58000,
  "cardCompany": "SHINHAN",
  "cardNumber": "1234-5678-****-****",
  "installmentMonth": 0
}
```

**Request 필드**:
| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| orderSeq | Long | O | 주문 ID |
| payMethod | String | O | 결제 수단 (CARD, TRANSFER, VBANK 등) |
| payAmount | Integer | O | 결제 금액 (원) |
| cardCompany | String | X | 카드사명 (카드 결제 시) |
| cardNumber | String | X | 카드번호 (카드 결제 시) |
| installmentMonth | Integer | X | 할부 개월 수 (0: 일시불) |

**Response (200 OK)**:
```json
{
  "paymentSeq": 1,
  "orderSeq": 1,
  "orderNo": "ORD-20260222-0001",
  "payMethod": "CARD",
  "payStatus": "PENDING",
  "payAmount": 58000,
  "pgProvider": "TOSS",
  "pgTid": "tid_1234567890",
  "cardCompany": "SHINHAN",
  "cardNumber": "1234-5678-****-****",
  "installmentMonth": 0,
  "approvedAt": null,
  "receiptUrl": "https://receipt.toss.im/..."
}
```

**Response 필드**:
| 필드 | 타입 | 설명 |
|------|------|------|
| paymentSeq | Long | 결제 ID |
| orderSeq | Long | 주문 ID |
| orderNo | String | 주문 번호 |
| payMethod | String | 결제 수단 |
| payStatus | String | 결제 상태 (PENDING, COMPLETED, FAILED, CANCELED) |
| payAmount | Integer | 결제 금액 (원) |
| pgProvider | String | PG사 정보 |
| pgTid | String | PG사 거래고유번호 |
| cardCompany | String | 카드사명 |
| cardNumber | String | 카드번호 |
| installmentMonth | Integer | 할부 개월 수 |
| approvedAt | LocalDateTime | 결제 승인 일시 |
| receiptUrl | String | 영수증 조회 URL |

---

### 2. 결제 확인 (승인)

```http
POST /v1/payments/{paymentSeq}/confirm
Authorization: Bearer {access_token}
```

**Path Parameters**:
| 파라미터 | 타입 | 필수 | 설명 |
|----------|------|------|------|
| paymentSeq | Long | O | 결제 ID |

**Response (200 OK)**:
```json
{
  "paymentSeq": 1,
  "orderSeq": 1,
  "orderNo": "ORD-20260222-0001",
  "payStatus": "COMPLETED",
  "payAmount": 58000,
  "approvedAt": "2026-02-22T23:06:00",
  "message": "결제가 완료되었습니다."
}
```

**Response 필드**:
| 필드 | 타입 | 설명 |
|------|------|------|
| paymentSeq | Long | 결제 ID |
| orderSeq | Long | 주문 ID |
| orderNo | String | 주문 번호 |
| payStatus | String | 결제 상태 (COMPLETED) |
| payAmount | Integer | 결제 금액 |
| approvedAt | LocalDateTime | 결제 확인 일시 |
| message | String | 처리 결과 메시지 |

> [!NOTE]
> Mock 구현이므로 항상 성공합니다. 실제 PG사 연동 시 승인 실패 케이스도 처리됩니다.

> [!TIP]
> 결제 확인 시 주문 상태가 자동으로 CONFIRMED(결제완료)로 변경됩니다.

---

## 상품 관리 API (SELLER)

> [!IMPORTANT]
> 모든 상품 관리 API는 **SELLER 권한**이 필요합니다.

### 1. 상품 등록

```http
POST /v1/seller/products
Authorization: Bearer {access_token}
Content-Type: application/json
```

**Request Body**:
```json
{
  "productName": "기본 면 티셔츠",
  "price": 29000,
  "description": "편안한 착용감의 기본 면 티셔츠입니다",
  "categoryCode": "FASHION_TOP",
  "stockQty": 100,
  "minOrderQty": 1
}
```

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| productName | String | O | 상품명 |
| price | Integer | O | 가격 (원) |
| description | String | O | 상품 설명 |
| categoryCode | String | O | 카테고리 코드 |
| stockQty | Integer | O | 재고 수량 |
| minOrderQty | Integer | X | 최소 주문 수량 (기본값: 1) |

**Response (200 OK)**:
```json
{
  "seq": 1,
  "productName": "기본 면 티셔츠",
  "price": 29000,
  "description": "편안한 착용감의 기본 면 티셔츠입니다",
  "categoryName": "상의",
  "categoryCode": "FASHION_TOP",
  "stockQty": 100,
  "status": "ON_SALE",
  "thumbnailUrl": null,
  "imageUrls": [],
  "viewCount": 0,
  "saleCount": 0,
  "marketName": "판매자's Shop"
}
```

---

### 2. 내 상품 목록 조회

```http
GET /v1/seller/products?page=0&size=10&sort=createdAt,desc
Authorization: Bearer {access_token}
```

**Query Parameters**:
| 파라미터 | 타입 | 필수 | 기본값 | 설명 |
|----------|------|------|--------|------|
| page | Integer | X | 0 | 페이지 번호 (0부터 시작) |
| size | Integer | X | 10 | 페이지 크기 |
| sort | String | X | createdAt,desc | 정렬 기준 (field,direction) |

**Response (200 OK)**:
```json
{
  "content": [
    {
      "seq": 1,
      "productName": "기본 면 티셔츠",
      "price": 29000,
      "description": "편안한 착용감의 기본 면 티셔츠입니다",
      "categoryName": "상의",
      "categoryCode": "FASHION_TOP",
      "stockQty": 100,
      "status": "ON_SALE",
      "thumbnailUrl": "https://via.placeholder.com/400",
      "imageUrls": ["https://via.placeholder.com/400"],
      "viewCount": 0,
      "saleCount": 0,
      "marketName": "판매자's Shop"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10
  },
  "totalPages": 1,
  "totalElements": 1,
  "last": true,
  "first": true
}
```

---

### 3. 내 상품 단건 조회

```http
GET /v1/seller/products/{productSeq}
Authorization: Bearer {access_token}
```

**Path Parameters**:
| 파라미터 | 타입 | 필수 | 설명 |
|----------|------|------|------|
| productSeq | Long | O | 상품 ID |

**Response (200 OK)**:
```json
{
  "seq": 1,
  "productName": "기본 면 티셔츠",
  "price": 29000,
  "description": "편안한 착용감의 기본 면 티셔츠입니다",
  "categoryName": "상의",
  "categoryCode": "FASHION_TOP",
  "stockQty": 100,
  "status": "ON_SALE",
  "thumbnailUrl": "https://via.placeholder.com/400",
  "imageUrls": ["https://via.placeholder.com/400"],
  "viewCount": 0,
  "saleCount": 0,
  "marketName": "판매자's Shop"
}
```

---

### 4. 상품 수정

```http
PUT /v1/seller/products/{productSeq}
Authorization: Bearer {access_token}
Content-Type: application/json
```

**Path Parameters**:
| 파라미터 | 타입 | 필수 | 설명 |
|----------|------|------|------|
| productSeq | Long | O | 상품 ID |

**Request Body**:
```json
{
  "productName": "프리미엄 면 티셔츠",
  "price": 39000,
  "description": "고급 원단을 사용한 프리미엄 티셔츠",
  "stockQty": 50,
  "status": "ON_SALE"
}
```

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| productName | String | X | 상품명 |
| price | Integer | X | 가격 (원) |
| description | String | X | 상품 설명 |
| stockQty | Integer | X | 재고 수량 |
| status | String | X | 상태 (ON_SALE, SOLD_OUT, HIDDEN) |

**Response (200 OK)**:
```json
{
  "seq": 1,
  "productName": "프리미엄 면 티셔츠",
  "price": 39000,
  "description": "고급 원단을 사용한 프리미엄 티셔츠",
  "categoryName": "상의",
  "categoryCode": "FASHION_TOP",
  "stockQty": 50,
  "status": "ON_SALE",
  "thumbnailUrl": "https://via.placeholder.com/400",
  "imageUrls": ["https://via.placeholder.com/400"],
  "viewCount": 0,
  "saleCount": 0,
  "marketName": "판매자's Shop"
}
```

---

### 5. 상품 삭제

```http
DELETE /v1/seller/products/{productSeq}
Authorization: Bearer {access_token}
```

**Path Parameters**:
| 파라미터 | 타입 | 필수 | 설명 |
|----------|------|------|------|
| productSeq | Long | O | 상품 ID |

**Response (204 No Content)**

---

### 6. 상품 이미지 업로드

```http
POST /v1/seller/products/{productSeq}/images
Authorization: Bearer {access_token}
Content-Type: multipart/form-data
```

**Path Parameters**:
| 파라미터 | 타입 | 필수 | 설명 |
|----------|------|------|------|
| productSeq | Long | O | 상품 ID |

**Form Data**:
| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| file | File | O | 업로드할 이미지 파일 (최대 10MB) |
| imageType | String | X | 이미지 타입 (MAIN, DETAIL, THUMBNAIL) 기본값: DETAIL |

**Response (200 OK)**:
```json
"/uploads/products/a1b2c3d4-e5f6-7890-abcd-ef1234567890.jpg"
```

> [!TIP]
> 이미지 타입
> - `MAIN`: 메인 이미지 (첫 번째 MAIN 이미지가 썸네일로 자동 설정)
> - `DETAIL`: 상세 이미지
> - `THUMBNAIL`: 썸네일 이미지

---

## 대시보드 API (SELLER)

> [!IMPORTANT]
> 모든 대시보드 API는 **SELLER 권한**이 필요합니다.

### 1. 대시보드 통계 조회

```http
GET /v1/seller/dashboard/stats
Authorization: Bearer {access_token}
```

**Response (200 OK)**:
```json
{
  "totalSales": 365000,
  "totalOrders": 5,
  "pendingOrders": 1,
  "totalProducts": 3,
  "todaySales": 153000,
  "todayOrders": 2,
  "monthSales": 303000,
  "monthOrders": 4
}
```

| 필드 | 타입 | 설명 |
|------|------|------|
| totalSales | Long | 총 매출액 (원) |
| totalOrders | Integer | 총 주문 수 |
| pendingOrders | Integer | 처리 대기 주문 수 (PENDING 상태) |
| totalProducts | Integer | 등록 상품 수 |
| todaySales | Long | 오늘 매출액 (원) |
| todayOrders | Integer | 오늘 주문 수 |
| monthSales | Long | 이번 달 매출액 (원) |
| monthOrders | Integer | 이번 달 주문 수 |

---

## 주문 관리 API (SELLER)

> [!IMPORTANT]
> 모든 주문 관리 API는 **SELLER 권한**이 필요합니다.

### 1. 최근 주문 조회

```http
GET /v1/seller/orders/recent
Authorization: Bearer {access_token}
```

**Response (200 OK)**:
```json
[
  {
    "orderId": 1,
    "orderNumber": "ORD-20260215-001",
    "customerName": "구매자",
    "productName": "기본 면 티셔츠",
    "quantity": 2,
    "totalPrice": 61000,
    "status": "PENDING",
    "createdAt": "2026-02-15T21:00:00"
  },
  {
    "orderId": 2,
    "orderNumber": "ORD-20260215-002",
    "customerName": "구매자",
    "productName": "무선 이어폰",
    "quantity": 1,
    "totalPrice": 92000,
    "status": "CONFIRMED",
    "createdAt": "2026-02-15T20:30:00"
  }
]
```

| 필드 | 타입 | 설명 |
|------|------|------|
| orderId | Long | 주문 ID |
| orderNumber | String | 주문 번호 |
| customerName | String | 주문자명 |
| productName | String | 상품명 (여러 상품인 경우 "상품명 외 N개") |
| quantity | Integer | 총 수량 |
| totalPrice | Integer | 총 금액 (원) |
| status | String | 주문 상태 (PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELED) |
| createdAt | LocalDateTime | 주문 일시 |

> [!NOTE]
> 최근 10건의 주문만 조회됩니다.

---

### 2. 판매자 주문 목록 조회

```http
GET /v1/seller/orders?page=0&size=10&status=PAYMENT_COMPLETED&startDate=2026-02-01&endDate=2026-02-28
Authorization: Bearer {access_token}
```

**Query Parameters**:
| 파라미터 | 타입 | 필수 | 기본값 | 설명 |
|----------|------|------|--------|------|
| page | Integer | X | 0 | 페이지 번호 (0부터 시작) |
| size | Integer | X | 10 | 페이지 크기 |
| status | String | X | - | 주문 상태 필터 (PENDING, PAYMENT_COMPLETED, PREPARING, SHIPPING, DELIVERED, CANCELED) |
| startDate | String | X | - | 주문 시작 날짜 (yyyy-MM-dd 형식) |
| endDate | String | X | - | 주문 종료 날짜 (yyyy-MM-dd 형식) |

**Response (200 OK)**:
```json
{
  "content": [
    {
      "orderSeq": 1,
      "orderNo": "ORD-20260215-001",
      "orderStatus": "PAYMENT_COMPLETED",
      "buyerName": "홍길동",
      "buyerPhone": "010-1234-5678",
      "totalPayAmount": 61000,
      "orderedAt": "2026-02-15T21:00:00",
      "firstItemName": "기본 면 티셔츠",
      "itemCount": 2,
      "shippingCompany": null,
      "trackingNumber": null
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10
  },
  "totalPages": 1,
  "totalElements": 1,
  "last": true,
  "first": true
}
```

**Response 필드**:
| 필드 | 타입 | 설명 |
|------|------|------|
| orderSeq | Long | 주문 ID |
| orderNo | String | 주문 번호 |
| orderStatus | String | 주문 상태 |
| buyerName | String | 구매자 이름 |
| buyerPhone | String | 구매자 전화번호 |
| totalPayAmount | Integer | 총 결제 금액 (원) |
| orderedAt | LocalDateTime | 주문 일시 |
| firstItemName | String | 첫 번째 상품명 |
| itemCount | Integer | 총 주문 상품 종류 수 |
| shippingCompany | String | 배송 회사명 (배송 중일 경우) |
| trackingNumber | String | 운송장 번호 (배송 중일 경우) |

> [!NOTE]
> 본인의 마켓에 속한 주문만 조회됩니다.

> [!TIP]
> 날짜 필터 사용 시 startDate와 endDate를 모두 지정하는 것을 권장합니다.

---

### 3. 판매자 주문 상세 조회

```http
GET /v1/seller/orders/{orderSeq}
Authorization: Bearer {access_token}
```

**Path Parameters**:
| 파라미터 | 타입 | 필수 | 설명 |
|----------|------|------|------|
| orderSeq | Long | O | 주문 ID |

**Response (200 OK)**:
```json
{
  "orderSeq": 1,
  "orderNo": "ORD-20260215-001",
  "orderStatus": "PAYMENT_COMPLETED",
  "totalProductAmount": 58000,
  "totalDiscountAmount": 0,
  "couponDiscountAmount": 0,
  "shippingFee": 3000,
  "totalPayAmount": 61000,
  "receiverName": "홍길동",
  "receiverPhone": "010-1234-5678",
  "zipCode": "06234",
  "address1": "서울시 강남구 테헤란로 123",
  "address2": "456호",
  "shippingMessage": "부재 시 문 앞에 놓아주세요",
  "buyerName": "주문자",
  "buyerEmail": "buyer@test.com",
  "buyerPhone": "010-9999-8888",
  "orderedAt": "2026-02-15T21:00:00",
  "paymentCompletedAt": "2026-02-15T21:05:00",
  "shippingStartedAt": null,
  "deliveredAt": null,
  "canceledAt": null,
  "cancelReason": null,
  "shippingCompany": null,
  "trackingNumber": null,
  "items": [
    {
      "seq": 1,
      "productSeq": 1,
      "productName": "기본 면 티셔츠",
      "itemOption": "Blue/XL",
      "unitPrice": 29000,
      "qty": 2,
      "itemAmount": 58000,
      "discountAmount": 0,
      "thumbnailUrl": "https://via.placeholder.com/400"
    }
  ]
}
```

**Response 필드**:
| 필드 | 타입 | 설명 |
|------|------|------|
| orderSeq | Long | 주문 ID |
| orderNo | String | 주문 번호 |
| orderStatus | String | 주문 상태 |
| totalProductAmount | Integer | 원 상품 금액 합계 |
| totalDiscountAmount | Integer | 총 할인 금액 |
| couponDiscountAmount | Integer | 쿠폰 할인 금액 |
| shippingFee | Integer | 배송비 |
| totalPayAmount | Integer | 최종 결제 금액 |
| receiverName | String | 받는 사람 이름 |
| receiverPhone | String | 받는 사람 전화번호 |
| zipCode | String | 우편번호 |
| address1 | String | 받는 주소 (기본) |
| address2 | String | 상세 주소 |
| shippingMessage | String | 배송 메모 |
| buyerName | String | 주문자 이름 |
| buyerEmail | String | 주문자 이메일 |
| buyerPhone | String | 주문자 전화번호 |
| orderedAt | LocalDateTime | 주문 일시 |
| paymentCompletedAt | LocalDateTime | 결제 완료 일시 |
| shippingStartedAt | LocalDateTime | 배송 시작 일시 |
| deliveredAt | LocalDateTime | 배송 완료 일시 |
| canceledAt | LocalDateTime | 주문 취소 일시 |
| cancelReason | String | 취소 사유 |
| shippingCompany | String | 배송 회사명 |
| trackingNumber | String | 운송장 번호 |
| items | List | 주문 상품 목록 |
| items[].seq | Long | 주문 상품 항목 ID |
| items[].productSeq | Long | 상품 ID |
| items[].productName | String | 상품명 |
| items[].itemOption | String | 선택 옵션 |
| items[].unitPrice | Integer | 단가 |
| items[].qty | Integer | 수량 |
| items[].itemAmount | Integer | 항목 금액 |
| items[].discountAmount | Integer | 항목 할인 금액 |
| items[].thumbnailUrl | String | 상품 썸네일 URL |

> [!IMPORTANT]
> 본인의 마켓에 속한 주문만 조회 가능합니다. 다른 판매자의 주문을 조회하면 403 Forbidden 오류가 발생합니다.

---

### 4. 주문 상태 변경

```http
PATCH /v1/seller/orders/{orderSeq}/status
Authorization: Bearer {access_token}
Content-Type: application/json
```

**Path Parameters**:
| 파라미터 | 타입 | 필수 | 설명 |
|----------|------|------|------|
| orderSeq | Long | O | 주문 ID |

**Request Body (일반 상태 변경)**:
```json
{
  "status": "PREPARING",
  "reason": "주문 확인 완료"
}
```

**Request Body (배송 시작 시)**:
```json
{
  "status": "SHIPPING",
  "shippingCompany": "CJ대한통운",
  "trackingNumber": "123456789012"
}
```

**Request 필드**:
| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| status | String | O | 변경할 주문 상태 (PREPARING, SHIPPING, DELIVERED, CANCELED) |
| reason | String | X | 상태 변경 사유 |
| shippingCompany | String | O* | 배송 회사명 (status가 SHIPPING일 때 필수) |
| trackingNumber | String | O* | 운송장 번호 (status가 SHIPPING일 때 필수) |

**Response (200 OK)**:
```json
{
  "orderSeq": 1,
  "orderNo": "ORD-20260215-001",
  "orderStatus": "PREPARING",
  "message": "주문 상태가 변경되었습니다."
}
```

**Response 필드**:
| 필드 | 타입 | 설명 |
|------|------|------|
| orderSeq | Long | 주문 ID |
| orderNo | String | 주문 번호 |
| orderStatus | String | 변경된 주문 상태 |
| message | String | 처리 결과 메시지 |

**주문 상태 전환 규칙**:
```
PAYMENT_COMPLETED → PREPARING (상품 준비 중)
PREPARING → SHIPPING (배송 시작)
SHIPPING → DELIVERED (배송 완료)
모든 상태 → CANCELED (주문 취소)
```

> [!IMPORTANT]
> - SHIPPING 상태로 변경 시 배송 정보(shippingCompany, trackingNumber)는 필수입니다.
> - 주문 상태 전환 규칙을 위반하면 400 Bad Request 오류가 발생합니다.
> - 이미 취소되거나 완료된 주문은 상태 변경이 불가능합니다.

> [!NOTE]
> 주문 취소 시 재고가 자동으로 복구되며, 결제 취소 처리도 함께 진행됩니다.

**에러 케이스**:
| 상황 | 상태 코드 | 메시지 |
|------|-----------|--------|
| 본인 마켓 주문이 아님 | 403 | 해당 주문에 대한 권한이 없습니다 |
| 잘못된 상태 전환 | 400 | 현재 주문 상태에서 해당 상태로 변경할 수 없습니다 |
| SHIPPING 시 배송정보 누락 | 400 | 배송 시작 시 배송 회사와 운송장 번호는 필수입니다 |
| 이미 취소된 주문 | 400 | 이미 취소된 주문입니다 |
| 주문을 찾을 수 없음 | 404 | 주문을 찾을 수 없습니다 |

---

### 5. 배송 정보 등록

```http
POST /v1/seller/orders/{orderSeq}/shipment
Authorization: Bearer {access_token}
Content-Type: application/json
```

**Path Parameters**:
| 파라미터 | 타입 | 필수 | 설명 |
|----------|------|------|------|
| orderSeq | Long | O | 주문 ID |

**Request Body**:
```json
{
  "shippingCompany": "CJ대한통운",
  "trackingNumber": "123456789012"
}
```

**Request 필드**:
| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| shippingCompany | String | O | 배송 회사명 (CJ대한통운, 우체국택배, 로젠택배 등) |
| trackingNumber | String | O | 운송장 번호 (숫자 12자리 이상) |

**Response (200 OK)**:
```json
{
  "orderSeq": 1,
  "orderNo": "ORD-20260215-001",
  "shippingCompany": "CJ대한통운",
  "trackingNumber": "123456789012",
  "orderStatus": "SHIPPING",
  "message": "배송 정보가 등록되었습니다."
}
```

**Response 필드**:
| 필드 | 타입 | 설명 |
|------|------|------|
| orderSeq | Long | 주문 ID |
| orderNo | String | 주문 번호 |
| shippingCompany | String | 배송 회사명 |
| trackingNumber | String | 운송장 번호 |
| orderStatus | String | 주문 상태 (SHIPPING으로 자동 변경) |
| message | String | 처리 결과 메시지 |

> [!TIP]
> 배송 정보 등록 시 주문 상태가 자동으로 SHIPPING으로 변경됩니다.

> [!NOTE]
> 이미 배송 정보가 등록된 주문의 경우, 기존 정보가 업데이트됩니다.

**에러 케이스**:
| 상황 | 상태 코드 | 메시지 |
|------|-----------|--------|
| 본인 마켓 주문이 아님 | 403 | 해당 주문에 대한 권한이 없습니다 |
| 배송 정보 등록 불가 상태 | 400 | 배송 정보를 등록할 수 없는 주문 상태입니다 |
| 운송장 번호 형식 오류 | 400 | 올바른 운송장 번호를 입력해주세요 |
| 주문을 찾을 수 없음 | 404 | 주문을 찾을 수 없습니다 |

**주요 배송 회사 목록**:
- CJ대한통운
- 우체국택배
- 로젠택배
- 한진택배
- 롯데택배
- 대한통운
- 경동택배
- 일양로지스

---

## 에러 코드

### HTTP 상태 코드
| 코드 | 의미 | 설명 |
|------|------|------|
| 200 | OK | 요청 성공 |
| 204 | No Content | 삭제 성공 (응답 본문 없음) |
| 400 | Bad Request | 잘못된 요청 (필수 파라미터 누락, 검증 실패 등) |
| 401 | Unauthorized | 인증 실패 (토큰 없음 또는 만료) |
| 403 | Forbidden | 권한 없음 |
| 404 | Not Found | 리소스를 찾을 수 없음 |
| 500 | Internal Server Error | 서버 오류 |

### 에러 응답 형식
```json
{
  "timestamp": "2026-02-15T20:30:00.123",
  "status": 400,
  "error": "Bad Request",
  "message": "상품명은 필수입니다",
  "path": "/v1/seller/products"
}
```

---

## 테스트 데이터

### 테스트 사용자
**BUYER**:
```
이메일: buyer@test.com
비밀번호: password
```

**SELLER**:
```
이메일: seller@test.com
비밀번호: password
```

### 카테고리 코드

**1depth (최상위)**
| 코드 | 이름 | seq |
|------|------|-----|
| `FASHION` | 패션/의류 | 1 |
| `ELECTRONICS` | 가전/디지털 | 2 |
| `FOOD` | 식품 | 3 |
| `BEAUTY` | 뷰티 | 4 |
| `HOME` | 가구/인테리어 | 5 |

**2depth (하위)**
| 코드 | 이름 | 부모 | seq |
|------|------|------|-----|
| `FASHION_TOP` | 상의 | 패션/의류 | 6 |
| `FASHION_BOTTOM` | 하의 | 패션/의류 | 7 |
| `FASHION_OUTER` | 아우터 | 패션/의류 | 8 |
| `FASHION_SHOES` | 신발 | 패션/의류 | 9 |
| `ELECTRONICS_AUDIO` | 오디오 | 가전/디지털 | 10 |
| `ELECTRONICS_MOBILE` | 모바일 | 가전/디지털 | 11 |

> [!NOTE]
> 상품 등록 시 `categoryCode` 필드에는 **2depth 코드**를 사용합니다.
