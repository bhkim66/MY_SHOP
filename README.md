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
- [x] 일반사용자, 마켓창업자 회원가입 / 로그인 + OAuth 연동(v2)
- [ ] 마켓 설립
- [ ] 상품 등록
- [ ] 주문 관리
- [ ] 결제 연동
- [ ] 일반 사용자 결제
- [ ] 쇼핑몰 관리자 
- [ ] AI 마켓 설명 (v2)

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
