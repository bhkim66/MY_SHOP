package com.my_shop.common.exception.custom;

import com.my_shop.common.exception.ErrorCode;

/**
 * 엔티티를 찾을 수 없을 때 발생하는 예외
 */
public class EntityNotFoundException extends BusinessException {

    public EntityNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }

    public EntityNotFoundException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public EntityNotFoundException(String entityName, Long id) {
        super(ErrorCode.ENTITY_NOT_FOUND, entityName + "을(를) 찾을 수 없습니다. ID: " + id);
    }
}
