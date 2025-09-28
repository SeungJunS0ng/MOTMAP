package com.motmap.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 공통 오류
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C001", "서버 내부 오류가 발생했습니다"),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "C002", "잘못된 요청입니다"),
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "C003", "입력값 검증 오류입니다"),

    // 맛집 관련 오류
    RESTAURANT_NOT_FOUND(HttpStatus.NOT_FOUND, "R001", "맛집을 찾을 수 없습니다"),
    DUPLICATE_RESTAURANT(HttpStatus.CONFLICT, "R002", "이미 등록된 맛집입니다"),
    INVALID_LOCATION(HttpStatus.BAD_REQUEST, "R003", "올바르지 않은 위치 좌표입니다"),
    INVALID_RATING(HttpStatus.BAD_REQUEST, "R004", "평점은 1-5점 사이여야 합니다"),
    INVALID_CATEGORY(HttpStatus.BAD_REQUEST, "R005", "올바르지 않은 카테고리입니다");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
