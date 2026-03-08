package com.my_shop.common.exception.custom;

import com.my_shop.common.exception.ErrorCode;

/**
 * 접근 권한이 없을 때 발생하는 예외
 */
public class AccessDeniedException extends BusinessException {

    public AccessDeniedException(ErrorCode errorCode) {
        super(errorCode);
    }

    public AccessDeniedException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public AccessDeniedException(String resource) {
        super(ErrorCode.ACCESS_DENIED, resource + "에 대한 접근 권한이 없습니다.");
    }
}
