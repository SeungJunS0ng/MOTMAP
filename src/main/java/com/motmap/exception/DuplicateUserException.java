package com.motmap.exception;

/**
 * 사용자 중복 예외
 */
public class DuplicateUserException extends BusinessException {

    public DuplicateUserException(String message) {
        super(ErrorCode.DUPLICATE_USER, message);
    }

    public DuplicateUserException() {
        super(ErrorCode.DUPLICATE_USER);
    }
}

