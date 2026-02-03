---
description: Spring Boot, JPA, MyBatis 환경을 위한 JUnit 5 테스트 생성 (통합 테스트 포함)
---

1. **대상 식별 (Target Identification)**
   - 사용자가 명시적으로 클래스나 함수를 지정했는지 확인합니다 (예: "/generate-test FcmService", "sendMessage 함수 테스트해줘").
   - **클래스 지정 시**: 해당 클래스 전체에 대한 단위 테스트를 생성합니다.
   - **함수/메서드 지정 시**: 해당 메서드의 로직에 집중하여 Happy Path, Edge Case, Error Handling을 모두 포함한 상세 테스트를 생성합니다.
   - **미지정 시**: 현재 열려있는(Active) 파일을 대상으로 합니다.

2. **코드 분석 (Context Analysis)**
   - 대상 파일(Service, Controller, Repository, Util 등)을 분석합니다.
   - 의존성(MyBatis Mapper, JPA Repository, 타 Service)을 식별합니다.

3. **테스트 전략 수립 (Test Strategy)**
   - **Service 테스트 (단위)**: `@ExtendWith(MockitoExtension.class)`를 사용하여 비즈니스 로직을 격리하여 테스트합니다.
   - **Util/Helper 테스트 (단위)**: 스프링 컨텍스트 없이 순수 JUnit 5로 테스트하며, `@ParameterizedTest`를 활용합니다.
   - **Controller 테스트 (단위/슬라이스)**: `MockMvc`를 사용하여 웹 계층을 검증합니다.
   - **Repository/Mapper 테스트 (슬라이스)**: `@DataJpaTest` 또는 `@MybatisTest`를 사용하여 DB 연동을 검증합니다.
   - **통합 테스트 (Integration)**: `@SpringBootTest`를 사용하여 전체 스프링 컨텍스트를 로드하고 컴포넌트 간의 상호작용을 검증합니다. `@Transactional`을 사용하여 테스트 격리를 보장합니다.

4. **테스트 시나리오 구성 (Test Scenarios)**
   - 모든 테스트는 다음 3가지 유형을 반드시 포함해야 합니다:
     - **HAPPY PATH**: 정상적인 입력과 흐름에서 기대한 결과가 나오는지 확인합니다.
     - **EDGE CASE**: 경계값, 빈 값, 매우 큰/작은 값 등 특수한 상황을 확인합니다.
     - **ERROR HANDLING**: 예외가 발생해야 하는 상황에서 적절한 Exception이 던져지고 처리되는지 확인합니다.

5. **테스트 코드 생성 가이드 (Generate Code Guide)**
   - **위치**: `src/test/java` 하위의 동일 패키지 구조
   - **기술 스택**: JUnit 5, AssertJ, Mockito
   - **네이밍**: 한글 `@DisplayName` 필수 사용.
   - **구조**: BDD 스타일 주석 (`// given`, `// when`, `// then`) 준수.
   - **통합 테스트 작성 시**: 실제 DB 호출보다는 H2 등을 사용하거나, 필요 시 TestContainer 사용을 고려하되 기본적으로는 `application-test.yml` 설정을 따릅니다.

6. **MyBatis/JPA 특화 처리**
   - MyBatis: Mapper의 SQL 매핑 정확성 검증.
   - JPA: Entity 상태 변화 및 영속성 컨텍스트 동작 검증.