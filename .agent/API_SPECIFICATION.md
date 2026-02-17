# API 명세서

MY_SHOP 프로젝트의 모든 REST API 엔드포인트를 문서화합니다.

---

## 목차
- [인증(Authentication)](#인증authentication)
- [회원 관리 API](#회원-관리-api)
- [카테고리 API](#카테고리-api)
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
