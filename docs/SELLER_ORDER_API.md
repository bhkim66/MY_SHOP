# 판매자 주문 관리 API 문서

## 개요
판매자가 자신의 마켓에 들어온 주문을 조회, 관리할 수 있는 API입니다.

## 공통 사항
- **Base URL**: `/v1/seller/orders`
- **인증**: Bearer Token (SELLER 권한 필요)
- **Content-Type**: `application/json`

---

## API 목록

### 1. 최근 주문 조회
판매자의 최근 주문 10건을 조회합니다.

**Endpoint**
```
GET /v1/seller/orders/recent
```

**Response**
```json
[
  {
    "orderId": 1,
    "orderNumber": "ORD20260228001",
    "customerName": "홍길동",
    "productName": "상품명 외 2개",
    "quantity": 5,
    "totalPrice": 50000,
    "status": "PAYMENT_COMPLETED",
    "createdAt": "2026-02-28T10:30:00"
  }
]
```

---

### 2. 주문 목록 조회 (페이징, 필터링)
판매자의 주문 목록을 페이징 및 필터링하여 조회합니다.

**Endpoint**
```
GET /v1/seller/orders
```

**Query Parameters**
| 파라미터 | 타입 | 필수 | 설명 | 예시 |
|---------|------|------|------|------|
| page | Integer | X | 페이지 번호 (0부터 시작) | 0 |
| size | Integer | X | 페이지 크기 (기본값: 20) | 20 |
| status | String | X | 주문 상태 필터 | PAYMENT_COMPLETED |
| startDate | DateTime | X | 시작 날짜 (ISO 8601) | 2026-02-01T00:00:00 |
| endDate | DateTime | X | 종료 날짜 (ISO 8601) | 2026-02-28T23:59:59 |

**주문 상태 값**
- `PENDING`: 주문 대기
- `PAYMENT_COMPLETED`: 결제 완료
- `PREPARING`: 상품 준비중
- `SHIPPING`: 배송중
- `DELIVERED`: 배송 완료
- `CANCELED`: 주문 취소

**Request Example**
```
GET /v1/seller/orders?page=0&size=20&status=PAYMENT_COMPLETED
GET /v1/seller/orders?startDate=2026-02-01T00:00:00&endDate=2026-02-28T23:59:59
```

**Response**
```json
{
  "content": [
    {
      "orderSeq": 1,
      "orderNo": "ORD20260228001",
      "buyerName": "홍길동",
      "receiverName": "홍길동",
      "receiverPhone": "010-1234-5678",
      "productName": "상품명 외 2개",
      "totalQuantity": 5,
      "totalPayAmount": 50000,
      "orderStatus": "PAYMENT_COMPLETED",
      "orderedAt": "2026-02-28T10:30:00"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20
  },
  "totalElements": 50,
  "totalPages": 3,
  "last": false
}
```

---

### 3. 주문 상세 조회
특정 주문의 상세 정보를 조회합니다.

**Endpoint**
```
GET /v1/seller/orders/{orderSeq}
```

**Path Parameters**
| 파라미터 | 타입 | 필수 | 설명 |
|---------|------|------|------|
| orderSeq | Long | O | 주문 ID |

**Response**
```json
{
  "orderSeq": 1,
  "orderNo": "ORD20260228001",
  "orderStatus": "SHIPPING",
  "orderedAt": "2026-02-28T10:30:00",
  "buyerInfo": {
    "name": "홍길동",
    "email": "hong@example.com",
    "phone": "010-1234-5678"
  },
  "receiverInfo": {
    "name": "홍길동",
    "phone": "010-1234-5678",
    "zipCode": "12345",
    "address1": "서울시 강남구",
    "address2": "테헤란로 123",
    "shippingMessage": "부재시 문앞에 놔주세요"
  },
  "orderItems": [
    {
      "itemSeq": 1,
      "itemName": "상품명1",
      "itemOption": "색상: 블랙, 사이즈: L",
      "unitPrice": 10000,
      "quantity": 2,
      "itemAmount": 20000,
      "itemStatus": "PENDING"
    }
  ],
  "paymentInfo": {
    "totalProductAmount": 50000,
    "totalDiscountAmount": 5000,
    "shippingFee": 3000,
    "totalPayAmount": 48000
  },
  "shipmentInfo": {
    "shippingCompany": "CJ대한통운",
    "trackingNumber": "123456789",
    "shippingStatus": "SHIPPING",
    "shippedAt": "2026-02-28T15:00:00",
    "deliveredAt": null
  },
  "statusHistory": [
    {
      "previousStatus": null,
      "newStatus": "PENDING",
      "changedReason": null,
      "createdAt": "2026-02-28T10:30:00"
    },
    {
      "previousStatus": "PENDING",
      "newStatus": "PAYMENT_COMPLETED",
      "changedReason": "결제 완료",
      "createdAt": "2026-02-28T10:31:00"
    }
  ]
}
```

---

### 4. 주문 상태 변경
주문의 상태를 변경합니다.

**Endpoint**
```
PATCH /v1/seller/orders/{orderSeq}/status
```

**Path Parameters**
| 파라미터 | 타입 | 필수 | 설명 |
|---------|------|------|------|
| orderSeq | Long | O | 주문 ID |

**Request Body**
```json
{
  "status": "PREPARING",
  "reason": "상품 준비 시작",
  "shippingCompany": "CJ대한통운",
  "trackingNumber": "123456789"
}
```

**Request 필드 설명**
| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| status | String | O | 변경할 주문 상태 (PREPARING, SHIPPING, DELIVERED) |
| reason | String | X | 상태 변경 사유 |
| shippingCompany | String | 조건부 | 배송 회사명 (SHIPPING 상태 변경 시 필수) |
| trackingNumber | String | 조건부 | 송장번호 (SHIPPING 상태 변경 시 필수) |

**상태 전환 규칙**
- `PAYMENT_COMPLETED` → `PREPARING`: 판매자가 주문 확인
- `PREPARING` → `SHIPPING`: 배송 시작 (배송 정보 필수)
- `SHIPPING` → `DELIVERED`: 배송 완료

**Request Examples**

1. 상품 준비 시작
```json
{
  "status": "PREPARING",
  "reason": "주문 확인 완료, 상품 준비 시작"
}
```

2. 배송 시작
```json
{
  "status": "SHIPPING",
  "reason": "배송 시작",
  "shippingCompany": "CJ대한통운",
  "trackingNumber": "123456789"
}
```

3. 배송 완료
```json
{
  "status": "DELIVERED",
  "reason": "배송 완료"
}
```

**Response**
```
200 OK
```

**Error Response**
```json
{
  "message": "잘못된 상태 전환입니다. 현재 상태: PENDING, 변경하려는 상태: SHIPPING"
}
```

---

### 5. 배송 정보 등록/수정
주문의 배송 정보를 등록하거나 수정합니다.

**Endpoint**
```
POST /v1/seller/orders/{orderSeq}/shipment
```

**Path Parameters**
| 파라미터 | 타입 | 필수 | 설명 |
|---------|------|------|------|
| orderSeq | Long | O | 주문 ID |

**Request Body**
```json
{
  "shippingCompany": "CJ대한통운",
  "trackingNumber": "123456789"
}
```

**Request 필드 설명**
| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| shippingCompany | String | O | 배송 회사명 |
| trackingNumber | String | O | 송장번호 |

**Response**
```
200 OK
```

---

## 주문 상태 흐름도

```
PENDING (주문 대기)
    ↓
PAYMENT_COMPLETED (결제 완료)
    ↓
PREPARING (상품 준비중) ← 판매자가 주문 확인
    ↓
SHIPPING (배송중) ← 판매자가 배송 정보 등록 및 배송 시작
    ↓
DELIVERED (배송 완료) ← 판매자가 배송 완료 처리
```

**취소 가능 상태**
- PENDING, PAYMENT_COMPLETED, PREPARING 상태에서만 주문 취소 가능

---

## 에러 코드

| HTTP Status | 에러 메시지 | 설명 |
|-------------|-----------|------|
| 400 | 유효하지 않은 주문 상태입니다 | 잘못된 주문 상태 값 |
| 400 | 잘못된 상태 전환입니다 | 현재 상태에서 변경할 수 없는 상태로 전환 시도 |
| 400 | 배송 정보가 필요합니다 | SHIPPING 상태 변경 시 배송 정보 누락 |
| 403 | 해당 주문에 대한 접근 권한이 없습니다 | 다른 판매자의 주문 접근 시도 |
| 404 | 주문을 찾을 수 없습니다 | 존재하지 않는 주문 ID |
| 404 | 판매자의 마켓을 찾을 수 없습니다 | 판매자의 마켓이 등록되지 않음 |

---

## 구현 파일 목록

### 엔티티
- `/MY_SHOP/src/main/java/com/my_shop/order/domain/entity/Order.java`
- `/MY_SHOP/src/main/java/com/my_shop/order/domain/entity/OrderItem.java`
- `/MY_SHOP/src/main/java/com/my_shop/order/domain/entity/OrderStatusHistory.java`
- `/MY_SHOP/src/main/java/com/my_shop/delivery/domain/entity/Shipment.java`

### Repository
- `/MY_SHOP/src/main/java/com/my_shop/order/infrastructure/OrderRepository.java`
- `/MY_SHOP/src/main/java/com/my_shop/order/infrastructure/OrderItemRepository.java`
- `/MY_SHOP/src/main/java/com/my_shop/order/infrastructure/OrderStatusHistoryRepository.java`
- `/MY_SHOP/src/main/java/com/my_shop/delivery/infrastructure/ShipmentRepository.java`

### DTO
- `/MY_SHOP/src/main/java/com/my_shop/seller/interfaces/dto/RecentOrderResponse.java`
- `/MY_SHOP/src/main/java/com/my_shop/seller/interfaces/dto/OrderListResponse.java`
- `/MY_SHOP/src/main/java/com/my_shop/seller/interfaces/dto/OrderDetailResponse.java`
- `/MY_SHOP/src/main/java/com/my_shop/seller/interfaces/dto/OrderStatusUpdateRequest.java`
- `/MY_SHOP/src/main/java/com/my_shop/seller/interfaces/dto/ShipmentRequest.java`

### Service & Controller
- `/MY_SHOP/src/main/java/com/my_shop/seller/application/OrderService.java`
- `/MY_SHOP/src/main/java/com/my_shop/seller/interfaces/controller/OrderController.java`
