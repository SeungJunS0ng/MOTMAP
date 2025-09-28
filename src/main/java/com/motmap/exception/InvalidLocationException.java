package com.motmap.exception;

public class InvalidLocationException extends BusinessException {

    public InvalidLocationException(String message) {
        super(ErrorCode.INVALID_LOCATION, message);
    }

    public InvalidLocationException(Double latitude, Double longitude) {
        super(ErrorCode.INVALID_LOCATION, "올바르지 않은 위치 좌표입니다. 위도: " + latitude + ", 경도: " + longitude);
    }
}
