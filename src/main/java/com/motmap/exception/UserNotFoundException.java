package com.motmap.exception;

/**
 * 사용자를 찾을 수 없음 예외
 */
public class UserNotFoundException extends BusinessException {

    public UserNotFoundException(String message) {
        super(ErrorCode.USER_NOT_FOUND, message);
    }

    public UserNotFoundException() {
        super(ErrorCode.USER_NOT_FOUND);
    }
}

