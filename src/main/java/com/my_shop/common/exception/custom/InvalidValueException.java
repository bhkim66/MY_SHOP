package com.my_shop.common.exception.custom;

import com.my_shop.common.exception.ErrorCode;

/**
 * 유효하지 않은 값일 때 발생하는 예외
 */
public class InvalidValueException extends BusinessException {

    public InvalidValueException(ErrorCode errorCode) {
        super(errorCode);
    }

    public InvalidValueException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public InvalidValueException(String message) {
        super(ErrorCode.INVALID_INPUT_VALUE, message);
    }
}
