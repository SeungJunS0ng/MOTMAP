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
    INVALID_CATEGORY(HttpStatus.BAD_REQUEST, "R005", "올바르지 않은 카테고리입니다"),

    // 인증/인가 관련 오류
    AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "A001", "인증에 실패했습니다"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "A002", "유효하지 않은 토큰입니다"),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "A003", "만료된 토큰입니다"),
    UNAUTHORIZED(HttpStatus.FORBIDDEN, "A004", "접근 권한이 없습니다"),

    // 사용자 관련 오류
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "사용자를 찾을 수 없습니다"),
    DUPLICATE_USER(HttpStatus.CONFLICT, "U002", "이미 존재하는 사용자입니다"),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "U003", "이미 사용중인 이메일입니다"),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "U004", "비밀번호가 일치하지 않습니다");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
