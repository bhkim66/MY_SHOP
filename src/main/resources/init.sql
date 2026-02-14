-- 사용자 정보 (비밀번호: Password123!)
INSERT INTO users (seq, login_id, password, name, email, phone, role, status, email_verified, phone_verified, created_at, created_by)
VALUES (1, 'admin', '$2a$10$CbFtgNmbQbnNZjTFPNQlXeYsqcRpSJAOjY7OqZSB.2giWP9G9Vmze', '관리자', 'admin@test.com', '010-0000-0000', 'ADMIN', 'ACTIVE', true, true, CURRENT_TIMESTAMP, 1);

INSERT INTO users (seq, login_id, password, name, email, phone, role, status, email_verified, phone_verified, created_at, created_by)
VALUES (2, 'buyer', '$2a$10$CbFtgNmbQbnNZjTFPNQlXeYsqcRpSJAOjY7OqZSB.2giWP9G9Vmze', '구매자', 'buyer@test.com', '010-1111-1111', 'BUYER', 'ACTIVE', true, true, CURRENT_TIMESTAMP, 2);

INSERT INTO users (seq, login_id, password, name, email, phone, role, status, email_verified, phone_verified, created_at, created_by)
VALUES (3, 'seller', '$2a$10$CbFtgNmbQbnNZjTFPNQlXeYsqcRpSJAOjY7OqZSB.2giWP9G9Vmze', '판매자', 'seller@test.com', '010-2222-2222', 'SELLER', 'ACTIVE', true, true, CURRENT_TIMESTAMP, 3);

-- 카테고리 정보
INSERT INTO categories (seq, category_code, category_name, depth, is_display, sort_order, created_at, created_by)
VALUES (1, 'FASHION', '패션/의류', 1, true, 1, CURRENT_TIMESTAMP, 1);

INSERT INTO categories (seq, category_code, category_name, depth, is_display, sort_order, created_at, created_by)
VALUES (2, 'ELECTRONICS', '가전/디지털', 1, true, 2, CURRENT_TIMESTAMP, 1);

INSERT INTO categories (seq, category_code, category_name, depth, is_display, sort_order, created_at, created_by)
VALUES (3, 'FOOD', '식품', 1, true, 3, CURRENT_TIMESTAMP, 1);

INSERT INTO categories (seq, category_code, category_name, depth, is_display, sort_order, created_at, created_by)
VALUES (4, 'BEAUTY', '뷰티', 1, true, 4, CURRENT_TIMESTAMP, 1);

INSERT INTO categories (seq, category_code, category_name, depth, is_display, sort_order, created_at, created_by)
VALUES (5, 'HOME', '가구/인테리어', 1, true, 5, CURRENT_TIMESTAMP, 1);

-- Identity 시작값 조정 (H2 용)
ALTER TABLE users ALTER COLUMN seq RESTART WITH 10;
ALTER TABLE categories ALTER COLUMN seq RESTART WITH 10;
