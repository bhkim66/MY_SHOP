# 로그인 및 회원가입 기능 구현 Walkthrough

Spring Security와 JWT를 활용하여 회원가입, 로그인, 토큰 재발급(Refresh Token) 기능을 구현했습니다.

## Changes

### 1. 회원 도메인 및 DTO
- **User Entity**: `role` (MemberRole), `status` (MemberStatus) Enum 적용. 테이블명 `users`로 변경 (H2 예약어 충돌 방지).
- **Enums**: `MemberRole` (BUYER, SELLER, ADMIN), `MemberStatus` (ACTIVE, INACTIVE 등).
- **DTOs**: `MemberRegisterRequest`, `LoginRequest`, `TokenRequest`, `MemberResponse`.

### 2. Spring Security & JWT
- **JwtTokenProvider**: Access Token(30분), Refresh Token(14일) 생성 및 검증 로직.
- **RefreshToken Entity**: Redis 대신 DB에 Refresh Token 저장 (MVP 단계).
- **SecurityConfig**: CSRF 비활성화, Session Stateless 설정, JWT 필터 등록.
- **CustomUserDetailsService**: DB에서 사용자 정보를 로드하여 `UserDetails` 반환.

### 3. 비즈니스 로직
- **MemberService**:
    - `signup`: 비밀번호 BCrypt 암호화 후 저장.
    - `login`: 인증 후 Access/Refresh Token 발급.
    - `reissue`: Refresh Token 검증 후 Access Token 재발급.
- **Controllers**:
    - `MemberController`: 회원가입 API.
    - `AuthController`: 로그인, 재발급 API.

### 4. 설정 및 기타
- **Gradle**: Spring Boot 3.2.1, JWT(jjwt 0.11.5) 의존성 추가.
- **JPA Auditing**: `JpaAuditConfig`를 통해 `createdAt`, `updatedAt` 자동 관리.

## Verification Results

### Automated Tests
`MemberIntegrationTest`를 통해 주요 시나리오를 검증했습니다.

1. **회원가입**: `POST /api/v1/members/signup`
    - 정상 요청 시 200 OK 및 회원 정보 반환 확인.
2. **로그인**: `POST /api/v1/auth/login`
    - 이메일/비밀번호 검증 후 Access Token, Refresh Token 반환 확인.
3. **토큰 재발급**: `POST /api/v1/auth/reissue`
    - 유효한 Refresh Token으로 Access Token 재발급 확인.

**Test Execution Log:**
```
MemberIntegrationTest > 회원가입 성공 테스트 PASSED
MemberIntegrationTest > 로그인 및 토큰 발급 테스트 PASSED
MemberIntegrationTest > 토큰 재발급 테스트 PASSED
BUILD SUCCESSFUL
```
