# MY_SHOP
나의 마켓

## 개요
개인 창업자들을 위한 쇼핑몰 이커머스 서비스

## 📌 범위
- B2C 이커머스 중심
- 물리 상품 기준 (디지털 상품은 추후 확장)

## 프로젝트 배경
MY_SHOP은 개발 지식 없이도
상품 등록부터 결제, 주문 관리까지
한 번에 운영할 수 있는 이커머스 서비스를 목표

## 핵심 기능
- 🛍️ 상품 등록 및 관리
- 🧾 주문 / 결제 / 정산 관리
- 📊 매출 및 통계 대시보드
- 🔐 사용자 인증 및 권한 관리
- 💻 AI 기반의 마켓 설명 서비스 제공

## 🚀 MVP 목표
- 상품 등록 → 주문 → 결제 → 주문 상태 변경까지 가능
- 관리자 1명 기준
- 단일 마켓, 단일 통화

 ## 🗺️ 개발 로드맵

### Phase 1: MVP - 주문/결제 (완료)
- [x] 회원가입 / 로그인 (JWT 인증)
- [x] 마켓 설립 (SELLER 가입 시 자동 생성)
- [x] 상품 등록 (SELLER)
- [x] 구매자 상품 조회 (공개 API)
- [x] 주문 관리
  - [x] 주문 생성
  - [x] 내 주문 목록/상세 조회
  - [x] 주문 취소
- [x] 결제 연동 (Mock 구현)
  - [x] 결제 요청
  - [x] 결제 확인
- [x] 판매자 대시보드
  - [x] 통계 조회
  - [x] 최근 주문 조회

### Phase 2: 확장 기능 (진행 중)
- [ ] OAuth 연동 (Google, Kakao, Naver)
- [ ] 실제 PG사 결제 연동 (토스페이먼츠/카카오페이)
- [ ] 판매자 주문 관리 (상태 변경, 배송 관리)
- [ ] 정산 시스템
- [ ] 리뷰 시스템
- [ ] AI 마켓 설명 생성

## 기술 스택
- BackEnd : Spring Boot
- FrontEnd : Vue.js
- ORM : JPA, Mybatis
- DB : MariaDB
- Auth: Spring Security + JWT, OAuth
- Infra: Aws, Docker, Nginx
- AI: OpenAI API

## 📁 프로젝트 구조
- user-domain
- product-domain
- order-domain
- settlement-domain

## 🤖 자동화
### AI 코드 리뷰 봇
- Pull Request 생성 시 자동으로 AI 기반 코드 리뷰 실행
- Anthropic Claude API를 활용한 코드 품질 검토
- GitHub Actions를 통한 자동화 워크플로우
