# Task: 회원가입 로직 구현 (Spring Security 기반)

## 📋 회원 도메인 분석 및 설계 [/]
- [x] 기존 `User` 엔티티 확인 및 요구사항에 맞게 수정 [x]
- [ ] `AccountStatus`, `MemberRole` 등 공통 코드 확인 [x]
- [x] 회원가입 DTO 설계 [x]

## 🛠️ 회원가입 로직 구현 [x]
- [x] 패스워드 암호화 로직 추가 (BCrypt) [x]
- [x] `MemberService.register()` 구현 [x]
- [x] `MemberController` API 엔드포인트 구현 (POST /api/v1/members/signup) [x]
- [x] 유효성 검사 (Email 중복, 필수값 등) [x]

## 🔐 로그인 및 인증 구현 (JWT) [x]
- [x] `JwtTokenProvider` 구현 (Access/Refresh Token 발급/검증) [x]
- [x] `RefreshToken` 엔티티 및 저장소 구현 [x]
- [x] `CustomUserDetailsService` 구현 [x]
- [x] `AuthController` 구현 (로그인, 토큰 재발급, 로그아웃) [x]
- [x] `JwtAuthenticationFilter` 구현 및 Security Config 등록 [x]

## 🛡️ Spring Security 설정 [x]
- [x] Security Config 설정 (BCryptPasswordEncoder 빈 등록 등) [x]
- [x] 회원가입 경로 허용 설정 [x]

## ✅ 검증 및 테스트 [x]
- [x] 회원가입 기능 통합 테스트 작성 [x]
- [x] 로그인 및 토큰 발급 테스트 [x]
- [x] 토큰 재발급(Refresh Token) 테스트 [x]
- [x] 입력값 유효성 검사 테스트 [x] -> 통합 테스트에 포함됨
- [x] 패스워드 암호화 저장 확인 [x] -> 통합 테스트로 검증됨
