package com.my_shop.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 애플리케이션 에러 코드 정의
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 공통 에러
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C001", "잘못된 입력값입니다."),
    INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "C002", "잘못된 타입입니다."),
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "C003", "엔티티를 찾을 수 없습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C004", "서버 오류가 발생했습니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "C005", "허용되지 않은 메서드입니다."),

    // 인증/인가 에러
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "A001", "인증이 필요합니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "A002", "접근 권한이 없습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "A003", "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "A004", "만료된 토큰입니다."),
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "A005", "로그인에 실패했습니다."),

    // 회원 에러
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "M001", "사용자를 찾을 수 없습니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "M002", "이미 사용 중인 이메일입니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "M003", "비밀번호가 일치하지 않습니다."),

    // 마켓 에러
    MARKET_NOT_FOUND(HttpStatus.NOT_FOUND, "MK001", "마켓을 찾을 수 없습니다."),
    MARKET_ACCESS_DENIED(HttpStatus.FORBIDDEN, "MK002", "해당 마켓에 대한 접근 권한이 없습니다."),

    // 상품 에러
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "P001", "상품을 찾을 수 없습니다."),
    PRODUCT_ACCESS_DENIED(HttpStatus.FORBIDDEN, "P002", "해당 상품에 대한 접근 권한이 없습니다."),
    INSUFFICIENT_STOCK(HttpStatus.BAD_REQUEST, "P003", "재고가 부족합니다."),

    // 주문 에러
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "O001", "주문을 찾을 수 없습니다."),
    ORDER_ACCESS_DENIED(HttpStatus.FORBIDDEN, "O002", "해당 주문에 대한 접근 권한이 없습니다."),
    INVALID_ORDER_STATUS(HttpStatus.BAD_REQUEST, "O003", "유효하지 않은 주문 상태입니다."),
    ORDER_CANNOT_CANCEL(HttpStatus.BAD_REQUEST, "O004", "취소할 수 없는 주문입니다."),
    INVALID_STATUS_TRANSITION(HttpStatus.BAD_REQUEST, "O005", "잘못된 상태 전환입니다."),

    // 결제 에러
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "PM001", "결제 정보를 찾을 수 없습니다."),
    PAYMENT_FAILED(HttpStatus.BAD_REQUEST, "PM002", "결제에 실패했습니다."),
    PAYMENT_AMOUNT_MISMATCH(HttpStatus.BAD_REQUEST, "PM003", "결제 금액이 일치하지 않습니다."),

    // 배송 에러
    SHIPMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "S001", "배송 정보를 찾을 수 없습니다."),
    SHIPMENT_INFO_REQUIRED(HttpStatus.BAD_REQUEST, "S002", "배송 정보가 필요합니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
