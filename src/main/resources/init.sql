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

-- 패션 하위 카테고리
INSERT INTO categories (seq, parent_seq, category_code, category_name, depth, is_display, sort_order, created_at, created_by)
VALUES (6, 1, 'FASHION_TOP', '상의', 2, true, 1, CURRENT_TIMESTAMP, 1);

INSERT INTO categories (seq, parent_seq, category_code, category_name, depth, is_display, sort_order, created_at, created_by)
VALUES (7, 1, 'FASHION_BOTTOM', '하의', 2, true, 2, CURRENT_TIMESTAMP, 1);

INSERT INTO categories (seq, parent_seq, category_code, category_name, depth, is_display, sort_order, created_at, created_by)
VALUES (8, 1, 'FASHION_OUTER', '아우터', 2, true, 3, CURRENT_TIMESTAMP, 1);

INSERT INTO categories (seq, parent_seq, category_code, category_name, depth, is_display, sort_order, created_at, created_by)
VALUES (9, 1, 'FASHION_SHOES', '신발', 2, true, 4, CURRENT_TIMESTAMP, 1);

-- 전자기기 하위 카테고리
INSERT INTO categories (seq, parent_seq, category_code, category_name, depth, is_display, sort_order, created_at, created_by)
VALUES (10, 2, 'ELECTRONICS_AUDIO', '오디오', 2, true, 1, CURRENT_TIMESTAMP, 1);

INSERT INTO categories (seq, parent_seq, category_code, category_name, depth, is_display, sort_order, created_at, created_by)
VALUES (11, 2, 'ELECTRONICS_MOBILE', '모바일', 2, true, 2, CURRENT_TIMESTAMP, 1);

-- Identity 시작값 조정 (H2 용)
ALTER TABLE users ALTER COLUMN seq RESTART WITH 10;
ALTER TABLE categories ALTER COLUMN seq RESTART WITH 20;

-- Market 데이터 (seller 유저용)
INSERT INTO markets (seq, owner_user_seq, market_name, market_slug, description, status, created_at, created_by)
VALUES (1, 3, '판매자''s Shop', 'seller-test-com', '테스트 판매자의 샵입니다', 'ACTIVE', CURRENT_TIMESTAMP, 3);

-- 상품 데이터
INSERT INTO products (seq, market_seq, category_seq, product_name, description, price, stock_qty, status, thumbnail_url, view_count, sale_count, review_count, min_order_qty, is_featured, created_at, created_by)
VALUES (1, 1, 1, '기본 면 티셔츠', '편안한 착용감의 기본 면 티셔츠입니다', 29000, 100, 'ON_SALE', 'https://via.placeholder.com/400', 0, 0, 0, 1, false, CURRENT_TIMESTAMP, 3);

INSERT INTO products (seq, market_seq, category_seq, product_name, description, price, stock_qty, status, thumbnail_url, view_count, sale_count, review_count, min_order_qty, is_featured, created_at, created_by)
VALUES (2, 1, 1, '프리미엄 후드티', '고급 원단을 사용한 프리미엄 후드티', 59000, 50, 'ON_SALE', 'https://via.placeholder.com/400', 10, 5, 0, 1, true, CURRENT_TIMESTAMP, 3);

INSERT INTO products (seq, market_seq, category_seq, product_name, description, price, stock_qty, status, thumbnail_url, view_count, sale_count, review_count, min_order_qty, is_featured, created_at, created_by)
VALUES (3, 1, 2, '무선 이어폰', '노이즈 캔슬링 기능이 있는 무선 이어폰', 89000, 30, 'ON_SALE', 'https://via.placeholder.com/400', 25, 12, 0, 1, true, CURRENT_TIMESTAMP, 3);

-- ProductImage 데이터
INSERT INTO product_images (seq, product_seq, image_url, image_type, sort_order, alt_text, created_at, created_by)
VALUES (1, 1, 'https://via.placeholder.com/400', 'MAIN', 0, '기본 면 티셔츠', CURRENT_TIMESTAMP, 3);

INSERT INTO product_images (seq, product_seq, image_url, image_type, sort_order, alt_text, created_at, created_by)
VALUES (2, 1, 'https://via.placeholder.com/400/detail1', 'DETAIL', 1, '기본 면 티셔츠 상세1', CURRENT_TIMESTAMP, 3);

INSERT INTO product_images (seq, product_seq, image_url, image_type, sort_order, alt_text, created_at, created_by)
VALUES (3, 2, 'https://via.placeholder.com/400', 'MAIN', 0, '프리미엄 후드티', CURRENT_TIMESTAMP, 3);

-- Order 데이터 (다양한 날짜와 상태)
-- 오늘 주문 (PENDING)
INSERT INTO orders (seq, order_no, market_seq, buyer_user_seq, order_status, total_product_amount, total_discount_amount, coupon_discount_amount, shipping_fee, total_pay_amount, receiver_name, receiver_phone, zip_code, address1, address2, buyer_name, buyer_email, buyer_phone, ordered_at, created_at, created_by)
VALUES (1, 'ORD-20260215-001', 1, 2, 'PENDING', 58000, 0, 0, 3000, 61000, '홍길동', '010-1111-1111', '12345', '서울시 강남구', '테헤란로 123', '구매자', 'buyer@test.com', '010-1111-1111', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 2);

-- 오늘 주문 (CONFIRMED)
INSERT INTO orders (seq, order_no, market_seq, buyer_user_seq, order_status, total_product_amount, total_discount_amount, coupon_discount_amount, shipping_fee, total_pay_amount, receiver_name, receiver_phone, zip_code, address1, address2, buyer_name, buyer_email, buyer_phone, ordered_at, created_at, created_by)
VALUES (2, 'ORD-20260215-002', 1, 2, 'CONFIRMED', 89000, 0, 0, 3000, 92000, '김철수', '010-2222-2222', '12345', '서울시 서초구', '서초대로 456', '구매자', 'buyer@test.com', '010-1111-1111', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 2);

-- 이번 달 초 주문
INSERT INTO orders (seq, order_no, market_seq, buyer_user_seq, order_status, total_product_amount, total_discount_amount, coupon_discount_amount, shipping_fee, total_pay_amount, receiver_name, receiver_phone, zip_code, address1, address2, buyer_name, buyer_email, buyer_phone, ordered_at, created_at, created_by)
VALUES (3, 'ORD-20260201-001', 1, 2, 'SHIPPED', 29000, 0, 0, 3000, 32000, '이영희', '010-3333-3333', '12345', '서울시 마포구', '마포대로 789', '구매자', 'buyer@test.com', '010-1111-1111', DATEADD('DAY', -14, CURRENT_TIMESTAMP), DATEADD('DAY', -14, CURRENT_TIMESTAMP), 2);

-- 이번 달 중순 주문
INSERT INTO orders (seq, order_no, market_seq, buyer_user_seq, order_status, total_product_amount, total_discount_amount, coupon_discount_amount, shipping_fee, total_pay_amount, receiver_name, receiver_phone, zip_code, address1, address2, buyer_name, buyer_email, buyer_phone, ordered_at, created_at, created_by)
VALUES (4, 'ORD-20260210-001', 1, 2, 'DELIVERED', 118000, 0, 0, 0, 118000, '박민수', '010-4444-4444', '12345', '서울시 송파구', '송파대로 321', '구매자', 'buyer@test.com', '010-1111-1111', DATEADD('DAY', -5, CURRENT_TIMESTAMP), DATEADD('DAY', -5, CURRENT_TIMESTAMP), 2);

-- 지난달 주문 (통계에서 제외되어야 함)
INSERT INTO orders (seq, order_no, market_seq, buyer_user_seq, order_status, total_product_amount, total_discount_amount, coupon_discount_amount, shipping_fee, total_pay_amount, receiver_name, receiver_phone, zip_code, address1, address2, buyer_name, buyer_email, buyer_phone, ordered_at, created_at, created_by)
VALUES (5, 'ORD-20260115-001', 1, 2, 'DELIVERED', 59000, 0, 0, 3000, 62000, '최지원', '010-5555-5555', '12345', '서울시 종로구', '종로 111', '구매자', 'buyer@test.com', '010-1111-1111', DATEADD('DAY', -30, CURRENT_TIMESTAMP), DATEADD('DAY', -30, CURRENT_TIMESTAMP), 2);

-- OrderItem 데이터
-- Order 1의 상품들 (티셔츠 2개)
INSERT INTO order_items (seq, order_seq, product_seq, item_name, item_option, unit_price, qty, item_amount, discount_amount, item_status, created_at, created_by)
VALUES (1, 1, 1, '기본 면 티셔츠', NULL, 29000, 2, 58000, 0, 'PENDING', CURRENT_TIMESTAMP, 2);

-- Order 2의 상품들 (이어폰 1개)
INSERT INTO order_items (seq, order_seq, product_seq, item_name, item_option, unit_price, qty, item_amount, discount_amount, item_status, created_at, created_by)
VALUES (2, 2, 3, '무선 이어폰', NULL, 89000, 1, 89000, 0, 'CONFIRMED', CURRENT_TIMESTAMP, 2);

-- Order 3의 상품들 (티셔츠 1개)
INSERT INTO order_items (seq, order_seq, product_seq, item_name, item_option, unit_price, qty, item_amount, discount_amount, item_status, created_at, created_by)
VALUES (3, 3, 1, '기본 면 티셔츠', NULL, 29000, 1, 29000, 0, 'SHIPPED', DATEADD('DAY', -14, CURRENT_TIMESTAMP), 2);

-- Order 4의 상품들 (후드티 2개)
INSERT INTO order_items (seq, order_seq, product_seq, item_name, item_option, unit_price, qty, item_amount, discount_amount, item_status, created_at, created_by)
VALUES (4, 4, 2, '프리미엄 후드티', NULL, 59000, 2, 118000, 0, 'DELIVERED', DATEADD('DAY', -5, CURRENT_TIMESTAMP), 2);

-- Order 5의 상품들 (후드티 1개)
INSERT INTO order_items (seq, order_seq, product_seq, item_name, item_option, unit_price, qty, item_amount, discount_amount, item_status, created_at, created_by)
VALUES (5, 5, 2, '프리미엄 후드티', NULL, 59000, 1, 59000, 0, 'DELIVERED', DATEADD('DAY', -30, CURRENT_TIMESTAMP), 2);

-- Identity 시작값 조정
ALTER TABLE markets ALTER COLUMN seq RESTART WITH 10;
ALTER TABLE products ALTER COLUMN seq RESTART WITH 10;
ALTER TABLE product_images ALTER COLUMN seq RESTART WITH 10;
ALTER TABLE orders ALTER COLUMN seq RESTART WITH 10;
ALTER TABLE order_items ALTER COLUMN seq RESTART WITH 10;
