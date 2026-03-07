# 회원가입 로직 구현 계획 (Spring Security 기반)

회원가입 기능을 구현하고 Spring Security를 연동하여 안전한 비밀번호 저장 및 초기 보안 설정을 진행합니다.

## Proposed Changes

### [Member Domain]

#### [MODIFY] [User.java](file:///Users/bh/project/MY_SHOP/src/main/java/com/my_shop/member/domain/entity/User.java)
- `role`과 `status`를 Enum(`MemberRole`, `MemberStatus`)으로 변경하여 타입 안정성 확보.
- SELLER 가입 시 필요한 추가 정보 처리를 위해 관련 로직 고려 (Market 엔티티와 연계).

#### [NEW] [MemberRole.java](file:///Users/bh/project/MY_SHOP/src/main/java/com/my_shop/member/domain/entity/MemberRole.java)
- `BUYER`, `SELLER`, `ADMIN` 권한 정의.

#### [NEW] [MemberStatus.java](file:///Users/bh/project/MY_SHOP/src/main/java/com/my_shop/member/domain/entity/MemberStatus.java)
- `ACTIVE`, `INACTIVE`, `SUSPENDED`, `WITHDRAWN` 상태 정의.

#### [NEW] [MemberRegisterRequest.java](file:///Users/bh/project/MY_SHOP/src/main/java/com/my_shop/member/interfaces/dto/MemberRegisterRequest.java)
- 회원가입 요청 데이터 수집 (Email, Password, Name, Phone, Role 등).
- SELLER인 경우 `shopName`, `businessNumber` 포함.

#### [NEW] [MemberService.java](file:///Users/bh/project/MY_SHOP/src/main/java/com/my_shop/member/application/MemberService.java)
- 회원가입 비즈니스 로직 (중복 체크, 비밀번호 암호화, 저장).
- SELLER 가입 시 `Market` 엔티티 생성 로직 포함.

#### [NEW] [MemberController.java](file:///Users/bh/project/MY_SHOP/src/main/java/com/my_shop/member/interfaces/MemberController.java)
- `POST /api/v1/members/signup` 엔드포인트 구현.

### [Authentication & Authorization (JWT)]

#### [NEW] [JwtTokenProvider.java](file:///Users/bh/project/MY_SHOP/src/main/java/com/my_shop/common/security/JwtTokenProvider.java)
- JWT 생성 (Access Token: 30분, Refresh Token: 2주).
- JWT 유효성 검증 및 Claims 추출.

#### [NEW] [RefreshToken.java](file:///Users/bh/project/MY_SHOP/src/main/java/com/my_shop/member/domain/entity/RefreshToken.java)
- `userId` (PK or Index), `token` (Refresh Token 값), `expiryDate` 저장.
- 로그인 시 생성/갱신, 로그아웃 시 삭제.

#### [NEW] [CustomUserDetailsService.java](file:///Users/bh/project/MY_SHOP/src/main/java/com/my_shop/common/security/CustomUserDetailsService.java)
- `UserDetailsService` 구현체. DB에서 사용자 정보 로드.

#### [NEW] [AuthController.java](file:///Users/bh/project/MY_SHOP/src/main/java/com/my_shop/member/interfaces/AuthController.java)
- `POST /api/v1/auth/login`: 이메일/비밀번호 검증 후 Access/Refresh Token 발급.
- `POST /api/v1/auth/reissue`: Refresh Token을 이용한 Access Token 재발급 (로그인 유지).
- `POST /api/v1/auth/logout`: Refresh Token 삭제.

#### [NEW] [JwtAuthenticationFilter.java](file:///Users/bh/project/MY_SHOP/src/main/java/com/my_shop/common/security/JwtAuthenticationFilter.java)
- 요청 헤더(`Authorization: Bearer ...`)에서 토큰 추출 및 검증.
- 유효한 토큰인 경우 `SecurityContextHolder`에 인증 정보 설정.

### [Security Configuration]

#### [NEW] [SecurityConfig.java](file:///Users/bh/project/MY_SHOP/src/main/java/com/my_shop/common/config/SecurityConfig.java)
- Spring Security 기본 설정 (CSRF 비활성화, Session Stateless).
- `BCryptPasswordEncoder` 빈 등록.
- `JwtAuthenticationFilter` 등록.
- `/api/v1/members/signup`, `/api/v1/auth/**` 경로 접근 허용.

## Verification Plan

### Automated Tests
- `MemberServiceTest`: 회원가입 성공 및 중복 이메일 실패 케이스 검증.
- `MemberControllerTest`: 회원가입 API 호출 시 HTTP 상태 코드 및 응답 확인.
- `BCryptPasswordEncoderTest`: 비밀번호 암호화 및 일치 여부 확인.

#### 실행 명령
```bash
./gradlew test --tests com.my_shop.member.*
```

### Manual Verification
1. Postman 또는 curl을 사용하여 회원가입 API 호출.
2. DB 콘솔을 통해 `user` 테이블에 패스워드가 암호화되어 저장되었는지 확인.
3. SELLER로 가입 시 `markets` 테이블에 상점 정보가 생성되었는지 확인.
