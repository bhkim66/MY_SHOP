# 스키마 변경 이력 (Schema Change History)

이 파일은 데이터베이스 스키마의 변경 사항을 날짜별로 기록하고 관리합니다.

## 2026-02-07
- **변경 사항**: `user` 테이블명을 `users`로 변경
- **관련 엔티티**: `User`
- **사유**: H2 데이터베이스 예약어(`user`)와의 충돌 해결 및 SQL 문법 오류 방지

## 2026-02-14
- **변경 사항**: 로컬 환경(H2) 데이터 자동 초기화 스크립트(`init.sql`) 추가
- **관련 설정**: `application-local.yml`, `init.sql`
- **사유**: 개발 및 테스트 효율성을 위한 초기 사용자 및 카테고리 데이터 자동 생성

## 2026-02-17
- **변경 사항**: `init.sql`에 2depth 카테고리 6개 추가
- **관련 파일**: `src/main/resources/init.sql`
- **사유**: 카테고리 API 구현 및 상품 등록 폼 UI 연동을 위한 초기 데이터 보강

**추가된 카테고리 데이터**:

| seq | category_code | category_name | depth | parent_seq | sort_order |
|-----|---------------|---------------|-------|------------|------------|
| 6 | FASHION_TOP | 상의 | 2 | 1 (FASHION) | 1 |
| 7 | FASHION_BOTTOM | 하의 | 2 | 1 (FASHION) | 2 |
| 8 | FASHION_OUTER | 아우터 | 2 | 1 (FASHION) | 3 |
| 9 | FASHION_SHOES | 신발 | 2 | 1 (FASHION) | 4 |
| 10 | ELECTRONICS_AUDIO | 오디오 | 2 | 2 (ELECTRONICS) | 1 |
| 11 | ELECTRONICS_MOBILE | 모바일 | 2 | 2 (ELECTRONICS) | 2 |

**Identity 시퀀스 조정**:
- `categories` 테이블: `RESTART WITH 20` (기존 11개 레코드 이후 자동 증가값 확보)
