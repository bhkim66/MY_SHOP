---
description: API 개발 시 명세서 자동 업데이트 규칙
---

# API 개발 시 명세서 업데이트 규칙

새로운 API 엔드포인트를 개발하거나 기존 API를 수정할 때는 반드시 API 명세서를 함께 업데이트합니다.

## 자동 수행 규칙

1. **새로운 Controller 또는 API 엔드포인트 생성 시**
   - `.agent/API_SPECIFICATION.md` 파일에 해당 API 명세 추가
   - 요청/응답 형식, 파라미터, 에러 코드 등 상세 정보 포함

2. **기존 API 수정 시**
   - `.agent/API_SPECIFICATION.md`에서 해당 API 섹션 업데이트
   - 변경된 Request/Response 스펙 반영

3. **API 삭제 시**
   - `.agent/API_SPECIFICATION.md`에서 해당 API 섹션 제거 또는 Deprecated 표시

## API 명세서 작성 형식

각 API는 다음 정보를 포함해야 합니다:

### 필수 정보
- HTTP Method와 Endpoint
- 인증 요구사항
- Request Body/Query Parameters (필드명, 타입, 필수여부, 설명)
- Response Body (성공 시)
- HTTP 상태 코드

### 선택 정보
- Path Parameters
- 에러 응답 예시
- 주의사항 (NOTE, IMPORTANT, WARNING 등)
- 예제 데이터

## 예제

```markdown
### 1. 상품 등록

\`\`\`http
POST /v1/seller/products
Authorization: Bearer {access_token}
Content-Type: application/json
\`\`\`

**Request Body**:
\`\`\`json
{
  "productName": "상품명",
  "price": 10000
}
\`\`\`

**Response (200 OK)**:
\`\`\`json
{
  "seq": 1,
  "productName": "상품명",
  "price": 10000
}
\`\`\`
```

## 참고 파일

- API 명세서: `.agent/API_SPECIFICATION.md`
- 요구사항 문서: `.agent/REQUIREMENTS.md`
