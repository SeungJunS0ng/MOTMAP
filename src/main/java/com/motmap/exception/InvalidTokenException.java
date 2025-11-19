package com.motmap.exception;

/**
 * JWT 토큰 무효 예외
 */
public class InvalidTokenException extends BusinessException {

    public InvalidTokenException(String message) {
        super(ErrorCode.INVALID_TOKEN, message);
    }

    public InvalidTokenException() {
        super(ErrorCode.INVALID_TOKEN);
    }
}

