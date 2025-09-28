package com.motmap.exception;

public class InvalidLocationException extends RuntimeException {
    public InvalidLocationException(String message) {
        super(message);
    }

    public InvalidLocationException(Double latitude, Double longitude) {
        super("올바르지 않은 위치 좌표입니다. 위도: " + latitude + ", 경도: " + longitude);
    }
}
