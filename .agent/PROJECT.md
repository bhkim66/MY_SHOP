# MY_SHOP 프로젝트 지침서

이 문서는 MY_SHOP 프로젝트의 핵심 설계 원칙과 기술적 가이드라인을 담고 있습니다. 모든 개발 작업은 이 문서를 최우선으로 참고하여 진행합니다.

---

## 1. 프로젝트 개요
- **프로젝트 목적**: 개인 창업자들을 위한 쇼핑몰 이커머스 서비스
- **비즈니스 도메인**: 쇼핑몰
- **주요 기능**:
    - 🛍️ 상품 등록 및 관리
    - 🧾 주문 / 결제 / 정산 관리
    - 📊 매출 및 통계 대시보드
    - 🔐 사용자 인증 및 권한 관리
    - 💻 AI 기반의 마켓 설명 서비스 제공

## 2. 기술 스택
- **BackEnd**: Spring Boot 3.x (Java 21)
- **FrontEnd**: React.js, Nginx
- **ORM**: JPA (Primary), QueryDSL, Mybatis (통계/정산 등 복잡한 쿼리용)
- **Database**:
    - **Prod/Dev**: MariaDB
    - **Local**: H2 (In-memory)
- **Auth**: Spring Security + JWT, OAuth 2.0
- **AI**: OpenAI API
- **Infra**: AWS, Docker

---

## 3. 아키텍처 및 설계 원칙 (JPA & DDD)

본 프로젝트는 **도메인 주도 설계(DDD)**와 **Layered Architecture**를 조화롭게 사용하여 확장 가능하고 유지보수가 쉬운 구조를 지향합니다.

### **3.1 패키지 구조 규칙 (Domain-Centric Structure)**

본 프로젝트는 도메인 간의 응집도를 높이기 위해 **도메인(Feature)을 최상위 패키지**로 두고, 그 하위에 계층(Layer)을 둡니다.

- **`com.my_shop.{domain}` (Domain Package)**:
    - `member`, `product`, `order`, `payment` 등 각 비즈니스 도메인이 최상위 패키지가 됩니다.
    
    - **`interfaces` (Web Layer)**:
        - 외부 요청과 응답을 담당합니다. (`controller`, `dto`, `facade`)
    
    - **`application` (Application Layer)**:
        - 비즈니스 유즈케이스 조립 및 트랜잭션 관리 (`service`)
    
    - **`domain` (Domain Layer)**:
        - 핵심 비즈니스 로직 및 데이터 (`entity`, `vo`, `repository` 인터페이스)
    
    - **`infrastructure` (Infrastructure Layer)**:
        - 기술적 구현체 (`persistence`, `external` API 클라이언트)

- **`com.my_shop.common` (Cross-cutting Package)**:
    - 특정 도메인에 종속되지 않는 공통 기능을 관리합니다.
    - `config`: JPA, Security, QueryDSL 등 전역 설정
    - `exception`: 전역 예외 처리 및 공통 예외 클래스
    - `utils`: 프로젝트 전반에서 사용되는 공통 유틸리티

- **`com.my_shop.{domain}` (Business Domain Packages)**:
    - `member`: 사용자 관리 (가입, 프로필, 권한)
    - `product`: 상품 관리 (카테고리, 옵션, 재고)
    - `order`: 주문 프로세스 (장바구니, 주문, 배송)
    - `payment`: 결제 처리 (승인, 취소, 환불)

    - **각 도메인 패키지 내부 계층 구조**:
        - `interfaces`: 외부 시스템과 통신하는 Web 레이어 (Controller, DTO, Mapper, Facade)
        - `application`: 비즈니스 로직을 조립하고 트랜잭션을 관리하는 서비스 레이어
        - `domain`: 비즈니스의 핵심 규칙이 담긴 엔티티 및 Repository 인터페이스 레이어
        - `infrastructure`: DB 저장 및 외부 연동의 구체적인 기술적 구현 레이어


### **3.2 레이어 간 참조 및 도메인 간 참조 규칙**
- **레이어 참조**: 상위 레이어는 하위 레이어를 참조할 수 있지만, 역방향 참조는 금지합니다. (`Interfaces -> Application -> Domain`)
- **도메인 참조**: 도메인 간의 직접적인 참조는 가급적 피하며, 필요할 경우 `Application` 레이어의 `Service` 또는 `Facade`를 통해 협력합니다.
- **DIP 적용**: `Domain` 레이어의 `Repository` 인터페이스는 `Infrastructure` 레이어에서 구현합니다.
- **엔티티 노출 제한**: 엔티티는 가급적 `Interfaces` 레이어까지 노출하지 않고 DTO를 통해 전달합니다.

### **3.3 네이밍 컨벤션 (Naming Convention)**
- **클래스명**: PascalCase (예: `MemberService`, `ProductRepository`)
- **메서드/변수명**: camelCase (예: `findMemberById`, `isSoldOut`)
- **Controller**: `~Controller` (예: `OrderController`)
- **Service**: `~Service` (예: `ProductService`)
- **Repository**: `~Repository` (예: `MemberRepository`)
- **DTO**: `~RequestDTO`, `~ResponseDTO` (예: `ProductSaveRequestDTO`)
- **Mapper**: `~Mapper`
- **Exception**: `~Exception` (예: `OrderNotFoundException`)

### **3.4 DDD 및 JPA 핵심 원칙**
- **Setter 지양**: 엔티티의 상태 변경은 비즈니스 의미가 담긴 메서드를 통함.
- **Lazy Loading**: 모든 연관 관계는 지연 로딩을 기본으로 함 (`FetchType.LAZY`).
- **Soft Delete**: 데이터 삭제 시 물리적 삭제 대신 논리적 삭제(`is_deleted`) 권장.

---

## 4. 데이터베이스 설계 (ERD)

본 프로젝트의 상세한 데이터베이스 설계 및 ERD는 다음 문서를 참고합니다.

- **상세 설계 문서**: [ERD_IMPROVEMENTS.md](file:///Users/bh/project/MY_SHOP/.agent/ERD_IMPROVEMENTS.md)

### **4.1 핵심 설계 방향**
- **확장성**: Phase 1(MVP)부터 Phase 3(고급 기능)까지 단계별 확장이 가능한 구조
- **성능**: 복합 인덱스 전략 및 슬로우 쿼리 방지를 위한 비정규화(통계 필드 등) 적용
- **데이터 정합성**: 주문 상태 이력 관리 및 결제/환불 상태의 엄격한 관리

---


## 5. 보안 정책 (Security & Auth)

### **5.1 인증(Authentication) 전략**
- **JWT (Stateless)**: 서버 부하를 줄이기 위해 Stateless 방식을 채택합니다.
    - **Access Token**: 짧은 만료 시간(30분~1시간), 클라이언트 매 요청마다 전달.
    - **Refresh Token**: 긴 만료 시간(14일), 데이터베이스에 저장하여 재발급 시 검증.
- **OAuth 2.0**: 소셜 로그인(Google, Kakao)을 지원하여 접근성을 높입니다.

### **5.2 인가(Authorization) 정책**
- **Role Hierarchy**: `ROLE_ADMIN > ROLE_SELLER > ROLE_BUYER`
- **End-point Security**:
    - `/api/v1/admin/**`: ADMIN 권한 필요
    - `/api/v1/seller/**`: SELLER 권한 이상 필요
    - `/api/v1/auth/**`: 인증 없이 접근 가능

---

## 6. 개발 규칙 및 컨벤션

### **6.1 코드 스타일**
- **Java**: Google Java Style Guide를 기본으로 따릅니다.
- **Lombok**: 적극 활용하되, `@Data` 사용은 지양하고 `@Getter`, `@RequiredArgsConstructor` 등을 개별 사용합니다.

### **6.2 네이밍 컨벤션**
- **DTO**: 요청은 `RequestDTO`, 응답은 `ResponseDTO` 접미사를 사용합니다.
- **Mapper**: 엔티티와 DTO 간의 변환 로직은 별도의 Mapper 클래스나 인터페이스를 통해 처리합니다.

### **6.3 테스트 작성 원칙**
- **JUnit 5**: 모든 비즈니스 로직에 대해 단위 테스트를 작성합니다.
- **Mockito**: 외부 의존성은 Mocking하여 테스트의 격리성을 보장합니다.
- **Test Fixture**: 재사용 가능한 테스트 데이터를 위해 별도의 Fixture 팩토리를 구성합니다.

### **6.4 슬로우 쿼리 및 성능 최적화**
- **QueryDSL**: 가급적 동적 쿼리는 QueryDSL을 사용하며, 실행 계획(Explain)을 확인합니다.
- **N+1 문제**: `fetchJoin`을 적절히 사용하여 불필요한 쿼리 발생을 막습니다.
- **Pagination**: 대량 데이터 조회 시 반드시 Pageable을 사용합니다.

---
> [!IMPORTANT]
> DB 구조 변경 시 반드시 관련 도메인 로직을 수정하고 ERD를 업데이트해야 합니다.